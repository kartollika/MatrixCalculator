package kartollika.matrixcalc.utilities;

import android.content.Context;
import android.os.Build;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import kartollika.matrixcalc.ITextOutputFormat;
import kartollika.matrixcalc.R;
import kartollika.matrixmodules.RationalNumber;
import kartollika.matrixmodules.matrix.AugmentedMatrix;
import kartollika.matrixmodules.matrix.DoubleMatrix;
import kartollika.matrixmodules.matrix.Matrix;

public class TableMatrixLayout extends LinearLayoutCompat {
    public static final String TAG = "TableMatrixLayout";

    public static final int READ_ONLY = 0;
    public static final int READ_WRITE = 1;

    public static final ITextOutputFormat DOUBLES_FORMATTER = new ITextOutputFormat() {
        @Override
        public String numberToString(Number number) {
            try {
                return String.valueOf(number.doubleValue());
            } catch (Exception e) {
                return String.valueOf(number);
            }
        }
    };
    public static final ITextOutputFormat RATIONALES_FORMATTER = new ITextOutputFormat() {
        @Override
        public String numberToString(Number number) {
            try {
                return RationalNumber.parseRational(number).toString();
            } catch (Exception e) {
                return String.valueOf(number);
            }
        }
    };
    private static final ITextOutputFormat defaultFormatter = new ITextOutputFormat() {
        @Override
        public String numberToString(Number number) {
            String stringValue = String.valueOf(number);
            if (stringValue.contains("E")) {
                stringValue = convertScientificToPlane(stringValue);
            }
            return stringValue;
        }
    };
    private ITextOutputFormat formatter = defaultFormatter;
    private int accessType = 1;
    private int curRows = 0;
    private int curColumns = 0;
    private boolean isAugmented = false;
    private List<List<Pair<EditTextMatrixCell, Number>>> cells = new ArrayList<>();
    private TableChangeListener tableChangeListener = new TableChangeListener() {
        @Override
        public void onTableChange() {
            //EMPTY
        }
    };

    public TableMatrixLayout(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
    }

    public TableMatrixLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TableMatrixLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private static String convertScientificToPlane(String raw) {
        Double formatted = Double.parseDouble(raw);
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#", decimalFormatSymbols);
        decimalFormat.setMaximumFractionDigits(8);
        return decimalFormat.format(formatted);
    }

    /**
     *
     */
    public void changeAccessType(int accessType) {
        this.accessType = accessType;
    }

    public void initTable(Matrix matrix) {
        if (matrix instanceof AugmentedMatrix) {
            setTable((AugmentedMatrix) matrix);
            return;
        }

        if (matrix instanceof DoubleMatrix) {
            setTable((DoubleMatrix) matrix);
            return;
        }

        if (matrix != null) {
            setTable(matrix);
            return;
        }

        throw new InvalidParameterException("matrix parameter is not a Matrix or its subclass");
    }

    public void setFormatter(ITextOutputFormat formatter) {
        this.formatter = formatter;
    }

    public void setTable(Matrix matrix) {
        reset();

        curRows = matrix.getRows();
        while (curColumns < matrix.getColumns()) {
            Matrix.RowIterator iterator = matrix.getRowIterator();
            iterator.skipTo(curColumns);
            addColumn(iterator);
        }
        tableChangeListener.onTableChange();
    }

    public void setTable(AugmentedMatrix augmentedMatrix) {
        reset();

        setTable((Matrix) augmentedMatrix);
        addAugmentedColumn(augmentedMatrix);
        isAugmented = true;
        tableChangeListener.onTableChange();
    }


    public void setTable(DoubleMatrix doubleMatrix) {
        reset();

        setTable(doubleMatrix.getMatrix1());
        addAugmentedMatrix(doubleMatrix.getMatrix2());
        tableChangeListener.onTableChange();
    }

    private void reset() {
        cells.clear();
        removeAllViews();
        curRows = 0;
        curColumns = 0;
    }

    public void addAugmentedMatrix(Matrix secondMatrix) {
        for (int i = 0; i < secondMatrix.getColumns(); ++i) {
            LinearLayout llAug = newLinearLayoutInstance();

            for (int j = 0; j < secondMatrix.getRows(); ++j) {
                FrameLayout editTextMatrixCell = newEditTextMatrixCellInstance(new DrawableSupplier() {
                    @Override
                    public Integer get() {
                        return R.drawable.back_extens;
                    }
                }, secondMatrix.getValue(j, i));
                llAug.addView(editTextMatrixCell);
            }
            addView(llAug);
        }
        tableChangeListener.onTableChange();
    }

    public void addAugmentedColumn(AugmentedMatrix augmentedMatrix) {
        LinearLayout ll = newLinearLayoutInstance();
        for (int i = 0; i < curRows; ++i) {
            FrameLayout editTextMatrixCell = newEditTextMatrixCellInstance(new DrawableSupplier() {
                @Override
                public Integer get() {
                    return R.drawable.back_extens;
                }
            }, augmentedMatrix.getValue(i, augmentedMatrix.getColumns()));
            ll.addView(editTextMatrixCell);
        }
        addView(ll);
        tableChangeListener.onTableChange();
    }

    public void addRow() {
        for (int i = 0; i < getChildCount(); ++i) {
            List<Pair<EditTextMatrixCell, Number>> columnsListMatrixCells = cells.get(i);
            final int curColumn = i;
            final LinearLayout row = (LinearLayout) getChildAt(i);
            FrameLayout editTextMatrixCell = newEditTextMatrixCellInstance(new DrawableSupplier() {
                @Override
                public Integer get() {
                    if (isAugmented && curColumn == curColumns) {
                        return R.drawable.back_extens;
                    }
                    return getMainDrawable(curColumn, curRows);
                }
            }, 0);

            columnsListMatrixCells.add(new Pair<>((EditTextMatrixCell) editTextMatrixCell.getChildAt(0), (Number) 0));
            row.addView(editTextMatrixCell);
        }

        curRows++;
        tableChangeListener.onTableChange();
    }

    public void deleteRow() {
        if (curRows == 1) {
            return;
        }

        for (int i = 0; i < getChildCount(); ++i) {
            LinearLayout ll = (LinearLayout) getChildAt(i);
            cells.get(i).remove(curRows - 1);
            ll.removeViewAt(curRows - 1);
        }

        curRows--;
        tableChangeListener.onTableChange();
    }

    public void addColumn() {
        addColumn(null);
    }

    public void addColumn(Matrix.RowIterator iterator) {
        LinearLayout newColumn = newLinearLayoutInstance();

        List<Pair<EditTextMatrixCell, Number>> newColumnListEditCells = new ArrayList<>();

        for (int i = 0; i < curRows; ++i) {
            final int curRow = i;

            Number next = iterator != null ? iterator.next() : 0;
            FrameLayout editTextMatrixCell = newEditTextMatrixCellInstance(new DrawableSupplier() {
                @Override
                public Integer get() {
                    return getMainDrawable(curColumns, curRow);
                }
            }, next);

            newColumnListEditCells.add(new Pair<>((EditTextMatrixCell) editTextMatrixCell.getChildAt(0), next));
            newColumn.addView(editTextMatrixCell);
        }
        if (isAugmented) {
            cells.add(curColumns, newColumnListEditCells);
            addView(newColumn, curColumns);
        } else {
            cells.add(newColumnListEditCells);
            addView(newColumn);
        }


        curColumns++;
        tableChangeListener.onTableChange();
    }

    private int getMainDrawable(int curI, int curRow) {
        if (curI == curRow) {
            return R.drawable.back_diagonal;
        }
        return R.drawable.back;
    }

    public void deleteColumn() {
        if (curColumns == 1) {
            return;
        }

        if (isAugmented) {
            removeViewAt(curColumns-- - 1);
            cells.remove(curColumns);
        } else {
            cells.remove(curColumns - 1);
            removeViewAt(--curColumns);
        }
        tableChangeListener.onTableChange();
    }

    private LinearLayout newLinearLayoutInstance() {
        LinearLayout instance = new LinearLayout(getContext());
        instance.setGravity(Gravity.CENTER);
        instance.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        instance.setLayoutParams(params);

        return instance;
    }

    private FrameLayout newEditTextMatrixCellInstance(DrawableSupplier supplier, Number number) {
        FrameLayout fl = (FrameLayout) LayoutInflater
                .from(getContext())
                .inflate(R.layout.table_item, null);

        final EditTextMatrixCell cellEditText = fl.findViewById(R.id.cell);

        cellEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                for (int i = start; i < start + count; ++i) {
                    Character c = s.charAt(i);
                    if (c.compareTo('-') == 0) {
                        cellEditText.setHasMinus(false);
                    }
                    if (c.compareTo('.') == 0 || c.compareTo('/') == 0) {
                        cellEditText.setHasDivider(false);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (int i = start; i < start + count; ++i) {
                    Character c = s.charAt(i);
                    if (c.compareTo('-') == 0) {
                        cellEditText.setHasMinus(true);
                    }
                    if (c.compareTo('.') == 0 || c.compareTo('/') == 0) {
                        cellEditText.setHasDivider(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cellEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditTextMatrixCell cell = (EditTextMatrixCell) v;
                String text = String.valueOf(cell.getText());
                if (hasFocus) {
                    if (text.equals("0")) {
                        cell.setText("");
                    }
                } else {
                    if (text.isEmpty()) {
                        cell.setText("0");
                    }
                }
            }
        });

        if (accessType == 0) {
            cellEditText.setInputType(InputType.TYPE_NULL);
            cellEditText.setFocusable(false);
        }


        cellEditText.setText(formatter.numberToString(number));

        cellEditText.setBackground(getResources().getDrawable(supplier.get()));

        cellEditText.setId(cells.size());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cellEditText.setShowSoftInputOnFocus(false);
        } else {
            cellEditText.setTextIsSelectable(true);
        }
        return fl;
    }

    public void setTableChangeListener(TableChangeListener tableChangeListener) {
        this.tableChangeListener = tableChangeListener;
    }

    public int getCurRows() {
        return curRows;
    }

    public int getCurColumns() {
        return curColumns;
    }

    public void updateNumbers(ITextOutputFormat iTextOutputFormat) {
        for (List<Pair<EditTextMatrixCell, Number>> pairList : cells) {
            for (Pair<EditTextMatrixCell, Number> pair : pairList) {
                if (pair.first == null) {
                    continue;
                }
                pair.first.setText(iTextOutputFormat.numberToString(pair.second));
            }
        }
    }

    public void updateNexts() {
        int cnt = 0;
        for (int i = 0; i < cells.size(); ++i) {
            List<Pair<EditTextMatrixCell, Number>> pairList = cells.get(i);
            for (int j = 0; j < pairList.size(); ++j) {
                EditTextMatrixCell cell = pairList.get(j).first;
                if (cell == null) return;

                if (i == curColumns - 1 && j == curRows - 1) {
                    cell.setId(cnt);
                    return;
                }

                cell.setId(cnt);
                cell.setNextFocusForwardId(++cnt);
            }
        }
    }

    public interface TableChangeListener {
        void onTableChange();
    }

    private interface DrawableSupplier {
        Integer get();
    }
}
