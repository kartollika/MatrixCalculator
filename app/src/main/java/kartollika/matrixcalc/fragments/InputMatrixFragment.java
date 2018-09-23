package kartollika.matrixcalc.fragments;

import android.app.Activity;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import kartollika.matrixcalc.MatrixManager;
import kartollika.matrixcalc.R;
import kartollika.matrixcalc.utilities.AnimationUtil;
import kartollika.matrixcalc.utilities.KeyboardKeyListener;
import kartollika.matrixcalc.utilities.TableMatrixLayout;
import kartollika.matrixmodules.matrix.Matrix;

import static kartollika.matrixcalc.fragments.DimensionPickerDialog.NEW_DIMENSION_VALUE;

public class InputMatrixFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    public static final String TAG = "InputMatrixFragment";

    private static final int TARGET_SET_ROWS = 0;
    private static final int TARGET_SET_COLUMNS = 1;
    private static final String REQUEST_SETTER_DIALOG = "set_dimension";

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

    private int curRows = 0;
    private int curColumns = 0;

    public static InputMatrixFragment newInstance() {

        Bundle args = new Bundle();

        InputMatrixFragment fragment = new InputMatrixFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_input_matrix, container, false);
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
        return v;
    }

    private void bindButtons() {
        Keyboard keyboard = new Keyboard(getActivity(), R.xml.keys);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(new KeyboardKeyListener());

        buttonHideSettings.setOnClickListener(this);
        buttonHideSettings.setOnLongClickListener(this);

        buttonDecRows.setOnClickListener(this);
        buttonSetRows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DimensionPickerDialog dimensionPickerDialog = DimensionPickerDialog.newInstance(1);
                dimensionPickerDialog.setTargetFragment(InputMatrixFragment.this, TARGET_SET_ROWS);
                dimensionPickerDialog.show(requireFragmentManager(), REQUEST_SETTER_DIALOG);
            }
        });
        buttonIncRows.setOnClickListener(this);

        buttonDecColumns.setOnClickListener(this);
        buttonSetColumns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DimensionPickerDialog dimensionPickerDialog = DimensionPickerDialog.newInstance(1);
                dimensionPickerDialog.setTargetFragment(InputMatrixFragment.this, TARGET_SET_COLUMNS);
                dimensionPickerDialog.show(requireFragmentManager(), REQUEST_SETTER_DIALOG);
            }
        });
        buttonIncColumns.setOnClickListener(this);

        buttonSave.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Matrix matrix = new Matrix(3, 3);
        table.setTableChangeListener(new TableMatrixLayout.TableChangeListener() {
            @Override
            public void onTableChange() {
                textViewCurrentRows.setText(getString(R.string.count_of_rows, table.getCurRows()));
                textViewCurrentColumns.setText(getString(R.string.count_of_columns, table.getCurColumns()));
            }
        });

        table.setTable(matrix);
    }

    /**
     * Save matrix to singleton Matrix Manager
     *
     * @return true if saving successful, false if unsuccessful
     */
    private boolean saveMatrix() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSetE: {

            }

            case R.id.buttonSet0: {

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
                    /*if (isNotificationRequired) {
                        Toast.makeText(getActivity(), R.string.hide_all_views, Toast.LENGTH_SHORT).show();
                    }*/
                } else {
                    buttonHideSettings.setText(R.string.hide_card);
                }
                break;
            }

            case R.id.saveButton: {
                MatrixManager matrixManager;
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
                curRows = data.getIntExtra(NEW_DIMENSION_VALUE, 3);
                Log.i(TAG, String.valueOf(curRows));
                updateTable();
                break;
            }

            case TARGET_SET_COLUMNS: {
                curColumns = data.getIntExtra(NEW_DIMENSION_VALUE, 3);
                updateTable();
            }
        }

    }

    private void updateTable() {

    }
}
