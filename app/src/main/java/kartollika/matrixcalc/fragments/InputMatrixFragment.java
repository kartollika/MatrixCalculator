package kartollika.matrixcalc.fragments;

import android.app.Activity;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import kartollika.matrixcalc.MatrixManager;
import kartollika.matrixcalc.R;
import kartollika.matrixcalc.utilities.AnimationUtil;
import kartollika.matrixcalc.utilities.EditTextMatrixCell;
import kartollika.matrixcalc.utilities.KeyboardKeyListener;
import kartollika.matrixcalc.utilities.TableMatrixLayout;
import kartollika.matrixmodules.RationalNumber;
import kartollika.matrixmodules.matrix.AugmentedMatrix;
import kartollika.matrixmodules.matrix.Matrix;

import static kartollika.matrixcalc.activities.InputMatrixActivity.KEY_HIDE_CARD_NOTIFICATION;
import static kartollika.matrixcalc.activities.InputMatrixActivity.KEY_MATRIX_TYPE;
import static kartollika.matrixcalc.fragments.DimensionPickerDialog.NEW_DIMENSION_VALUE;

public class InputMatrixFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    public static final String TAG = "InputMatrixFragment";

    private static final int TARGET_SET_ROWS = 0;
    private static final int TARGET_SET_COLUMNS = 1;
    private static final String REQUEST_SETTER_DIALOG = "set_dimension";

    private int matrixType;

    private ViewGroup root;
    private ViewGroup fullCard;
    private Group notFullCard;

    private KeyboardView keyboardView;
    private Button buttonSave;
    private Button buttonHideSettings;
    private Button buttonSetE;
    private Button buttonSet0;
    private TextView textViewCurrentRows;
    private Button buttonIncRows;
    private Button buttonSetRows;
    private Button buttonDecRows;
    private TextView textViewCurrentColumns;
    private Button buttonIncColumns;
    private Button buttonSetColumns;
    private Button buttonDecColumns;
    private TableMatrixLayout table;

    private boolean isUpperCardFullyHidden;
    private boolean isUpperCardHidden;
    private boolean isNotificationRequired;
    private int switchCardCounter = 0;

    private int curRows = 0;
    private int curColumns = 0;
    private Matrix matrix;

    public static InputMatrixFragment newInstance(int type) {

        Bundle args = new Bundle();
        args.putInt(KEY_MATRIX_TYPE, type);
        InputMatrixFragment fragment = new InputMatrixFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        matrixType = getArguments().getInt(KEY_MATRIX_TYPE);
        isNotificationRequired = PreferenceManager
                .getDefaultSharedPreferences(requireContext())
                .getBoolean(KEY_HIDE_CARD_NOTIFICATION, true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_input_matrix, container, false);
        keyboardView = v.findViewById(R.id.kv);
        buttonSave = v.findViewById(R.id.saveButton);
        buttonHideSettings = v.findViewById(R.id.hideCard);
        buttonSetE = v.findViewById(R.id.buttonSetE);
        buttonSet0 = v.findViewById(R.id.buttonSet0);

        textViewCurrentRows = v.findViewById(R.id.textViewCurrentRows);
        buttonIncRows = v.findViewById(R.id.row_plus);
        buttonSetRows = v.findViewById(R.id.rowCount);
        buttonDecRows = v.findViewById(R.id.row_minus);

        textViewCurrentColumns = v.findViewById(R.id.textViewCurrentColumns);
        buttonIncColumns = v.findViewById(R.id.column_plus);
        buttonSetColumns = v.findViewById(R.id.columnCount);
        buttonDecColumns = v.findViewById(R.id.column_minus);

        root = v.findViewById(R.id.constraintRoot);
        fullCard = v.findViewById(R.id.fullCardGroup);
        notFullCard = v.findViewById(R.id.notFullCardGroup);

        table = v.findViewById(R.id.table);
        bindButtons();

        HorizontalScrollView hsv = v.findViewById(R.id.horScroll);
        hsv.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        hsv.setFocusable(true);
        hsv.setFocusableInTouchMode(true);
        hsv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                return false;
            }
        });

        ScrollView sv = v.findViewById(R.id.verScroll);
        sv.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        sv.setFocusable(true);
        sv.setFocusableInTouchMode(true);
        sv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                return false;
            }
        });
        return v;
    }

    private void bindButtons() {
        Keyboard keyboard = new Keyboard(getActivity(), R.xml.keys);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(new KeyboardKeyListener(requireActivity()));

        buttonHideSettings.setOnClickListener(this);
        buttonHideSettings.setOnLongClickListener(this);

        buttonDecRows.setOnClickListener(this);
        buttonSetRows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DimensionPickerDialog dimensionPickerDialog = DimensionPickerDialog.newInstance(curRows);
                dimensionPickerDialog.setTargetFragment(InputMatrixFragment.this, TARGET_SET_ROWS);
                dimensionPickerDialog.show(requireFragmentManager(), REQUEST_SETTER_DIALOG);
            }
        });
        buttonIncRows.setOnClickListener(this);

        buttonDecColumns.setOnClickListener(this);
        buttonSetColumns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DimensionPickerDialog dimensionPickerDialog = DimensionPickerDialog.newInstance(curColumns);
                dimensionPickerDialog.setTargetFragment(InputMatrixFragment.this, TARGET_SET_COLUMNS);
                dimensionPickerDialog.show(requireFragmentManager(), REQUEST_SETTER_DIALOG);
            }
        });
        buttonIncColumns.setOnClickListener(this);

        buttonSetE.setOnClickListener(this);
        buttonSet0.setOnClickListener(this);

        buttonSave.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MatrixManager matrixManager = MatrixManager.getInstance(requireContext());
        table.setTableChangeListener(new TableMatrixLayout.TableChangeListener() {
            @Override
            public void onTableChange() {
                textViewCurrentRows.setText(getString(R.string.count_of_rows, table.getCurRows()));
                curRows = table.getCurRows();

                textViewCurrentColumns.setText(getString(R.string.count_of_columns, table.getCurColumns()));
                curColumns = table.getCurColumns();
            }
        });


        switch (matrixType) {
            case 0: {
                matrix = matrixManager.getA();
                break;
            }

            case 1: {
                matrix = matrixManager.getB();
                break;
            }

            case 2: {
                matrix = matrixManager.getMatrixSystem();
                break;
            }

            default: {
                matrix = new Matrix(3, 3);
            }
        }
        table.initTable(matrix);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSetE: {
                Matrix matrixE;
                if (matrixType == 2) {
                    matrixE = new AugmentedMatrix(curRows, curColumns, 1);
                } else {
                    matrixE = new Matrix(curRows, curColumns, 1);
                }
                table.initTable(matrixE);
                break;
            }

            case R.id.buttonSet0: {
                Matrix matrix0;
                if (matrixType == 2) {
                    matrix0 = new AugmentedMatrix(curRows, curColumns);
                } else {
                    matrix0 = new Matrix(curRows, curColumns);
                }
                table.initTable(matrix0);
                break;
            }

            case R.id.row_minus: {
                table.deleteRow();
                break;
            }

            case R.id.row_plus: {
                table.addRow();
                break;
            }

            case R.id.column_minus: {
                table.deleteColumn();
                break;
            }

            case R.id.column_plus: {
                table.addColumn();
                break;
            }

            case R.id.hideCard: {
                if (isUpperCardFullyHidden) {
                    isUpperCardFullyHidden = AnimationUtil.switchFullCardVisibility(root, fullCard);
                    if (isUpperCardHidden) {
                        buttonHideSettings.setText(R.string.show_card);
                    } else {
                        buttonHideSettings.setText(R.string.hide_card);
                    }
                    break;
                }

                isUpperCardHidden = AnimationUtil.switchControlCardVisibility(root, notFullCard);
                if (isUpperCardHidden) {
                    buttonHideSettings.setText(R.string.show_card);
                    if (switchCardCounter++ < 2) {
                        if (isNotificationRequired) {
                            Toasty.info(requireContext(), getString(R.string.hide_all_views), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        isNotificationRequired = false;
                    }
                } else {
                    buttonHideSettings.setText(R.string.hide_card);
                }
                break;
            }

            case R.id.saveButton: {
                try {
                    saveMatrix();
                    Toasty.success(requireContext(), "Successfully saved").show();
                    requireActivity().finish();
                } catch (NumberFormatException nfe) {
                    Toasty.error(requireContext(), nfe.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Save matrix to singleton Matrix Manager
     */
    private void saveMatrix() {
        Matrix newMatrix;
        Number[][] values = new Number[curRows][curColumns];
        Number[] coefficients;
        for (int i = 0; i < (matrixType == 2 ? table.getChildCount() - 1 : table.getChildCount()); ++i) {
            LinearLayout child = (LinearLayout) table.getChildAt(i);
            for (int j = 0; j < child.getChildCount(); ++j) {
                EditTextMatrixCell cell = child.getChildAt(j).findViewById(R.id.cell);
                String text = String.valueOf(cell.getText());
                Number number;
                try {
                    number = Long.parseLong(text);
                    checkRationaleNumberAsString(number.toString(), 18);
                } catch (NumberFormatException nfeInteger) {
                    try {
                        number = Double.parseDouble(text);
                        checkRationaleNumberAsString(number.toString(), 18);
                    } catch (NumberFormatException nfeDouble) {
                        try {
                            number = RationalNumber.parseRational(text);
                        } catch (NumberFormatException nfeRational) {
                            throw new NumberFormatException(getString(R.string.invalid_value_in_cell, j + 1, i + 1));
                        }
                    }
                }
                values[j][i] = number;
            }
        }

        if (matrixType == 2) {
            coefficients = new Number[curRows];
            LinearLayout coefficientColumn = (LinearLayout) table.getChildAt(table.getChildCount() - 1);
            for (int i = 0; i < curRows; ++i) {
                EditTextMatrixCell cell = coefficientColumn.getChildAt(i).findViewById(R.id.cell);
                String text = String.valueOf(cell.getText());
                Number number;
                try {
                    number = Integer.parseInt(text);
                    checkRationaleNumberAsString(number.toString(), 18);
                } catch (NumberFormatException nfeInteger) {
                    try {
                        number = Double.parseDouble(text);
                        checkRationaleNumberAsString(number.toString(), 18);
                    } catch (NumberFormatException nfeDouble) {
                        try {
                            number = RationalNumber.parseRational(text);
                        } catch (NumberFormatException nfeRational) {
                            throw new NumberFormatException(getString(R.string.invalid_value_in_cell,
                                    i + 1, table.getChildCount()));
                        }
                    }
                }
                coefficients[i] = number;
            }
            newMatrix = new AugmentedMatrix(values, coefficients);
        } else {
            newMatrix = new Matrix(values);
        }

        passIntoManager(newMatrix);
    }

    private void checkRationaleNumberAsString(String numberString, int maxExponentValue) {
        if (numberString.contains("E")) {
            int exponent = Integer.parseInt(numberString.substring(numberString.indexOf('E'), numberString.length() - 1));
            if (exponent >= maxExponentValue) {
                throw new NumberFormatException("Number is very big");
            }
        }
    }

    private void passIntoManager(Matrix newMatrix) {
        MatrixManager matrixManager = MatrixManager.getInstance(requireContext());
        switch (matrixType) {
            case 0: {
                matrixManager.setA(newMatrix);
                break;
            }

            case 1: {
                matrixManager.setB(newMatrix);
                break;
            }

            case 2: {
                matrixManager.setSystem((AugmentedMatrix) newMatrix);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.hideCard: {
                isUpperCardFullyHidden = AnimationUtil.switchFullCardVisibility(root, fullCard);
                if (isUpperCardFullyHidden) {
                    buttonHideSettings.setText(getString(R.string.show_card));
                    isNotificationRequired = false;
                } else {
                    buttonHideSettings.setText(getString(R.string.hide_card));
                }
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;

        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case TARGET_SET_ROWS: {
                int newRows = data.getIntExtra(NEW_DIMENSION_VALUE, 3);
                updateTable(newRows, curColumns);
                break;
            }

            case TARGET_SET_COLUMNS: {
                int newCurColumns = data.getIntExtra(NEW_DIMENSION_VALUE, 3);
                updateTable(curRows, newCurColumns);
            }
        }

    }

    private void updateTable(int newRows, int newColumns) {
        if (newRows > curRows) {
            while (curRows < newRows) {
                table.addRow();
            }
        } else if (newRows < curRows) {
            while (curRows > newRows) {
                table.deleteRow();
            }
        }

        if (newColumns > curColumns) {
            while (curColumns < newColumns) {
                table.addColumn();
            }
        } else if (newColumns < curColumns) {
            while (curColumns > newColumns) {
                table.deleteColumn();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
                .putBoolean(KEY_MATRIX_TYPE, isNotificationRequired)
                .apply();
    }
}
