package kartollika.matrixcalc.utilities;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import kartollika.matrixcalc.R;
import kartollika.matrixmodules.matrix.AugmentedMatrix;
import kartollika.matrixmodules.matrix.DoubleMatrix;
import kartollika.matrixmodules.matrix.Matrix;

public class TableMatrixLayout extends LinearLayoutCompat {

    private int curRows = 0;
    private int curColumns = 0;
    private boolean isAugmented = false;
    private TableChangeListener tableChangeListener;

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

    private void reset() {
        removeAllViews();
        curRows = 0;
        curColumns = 0;
    }

    public void setTable(DoubleMatrix doubleMatrix) {
        reset();

        setTable(doubleMatrix.getMatrix1());
        addAugmentedMatrix(doubleMatrix.getMatrix2());
        tableChangeListener.onTableChange();
    }

    public void addAugmentedMatrix(Matrix secondMatrix) {
        for (int i = 0; i < secondMatrix.getColumns(); ++i) {
            LinearLayout llAug = newLinearLayoutInstance();

            for (int j = 0; j < secondMatrix.getColumns(); ++j) {
                FrameLayout editTextMatrixCell = newEditTextMatrixCellInstance(new DrawableSupplier() {
                    @Override
                    public Integer get() {
                        return R.drawable.back_extens;
                    }
                });
                llAug.addView(editTextMatrixCell);
            }
            addView(llAug);
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

    public void addAugmentedColumn(AugmentedMatrix augmentedMatrix) {
        LinearLayout ll = newLinearLayoutInstance();
        for (int i = 0; i < curRows; ++i) {
            FrameLayout editTextMatrixCell = newEditTextMatrixCellInstance(new DrawableSupplier() {
                @Override
                public Integer get() {
                    return R.drawable.back_extens;
                }
            });
            ll.addView(editTextMatrixCell);
        }
        addView(ll);
        tableChangeListener.onTableChange();
    }

    public void addRow() {
        for (int i = 0; i < getChildCount(); ++i) {
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
            });

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
        for (int i = 0; i < curRows; ++i) {
            final int curRow = i;
            FrameLayout editTextMatrixCell = newEditTextMatrixCellInstance(new DrawableSupplier() {
                @Override
                public Integer get() {
                    return getMainDrawable(curColumns, curRow);
                }
            });
            EditTextMatrixCell cell = editTextMatrixCell.findViewById(R.id.cell);

            if (iterator != null) {
                if (iterator.hasNext()) {
                    cell.setText(String.valueOf(iterator.next()));
                }
            }

            newColumn.addView(editTextMatrixCell);
        }
        if (isAugmented) {
            addView(newColumn, curRows);
        } else {
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
            removeViewAt(curColumns-- - 2);
            return;
        }
        removeViewAt(--curColumns);

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

    private FrameLayout newEditTextMatrixCellInstance(DrawableSupplier supplier) {
        FrameLayout fl = (FrameLayout) LayoutInflater
                .from(getContext())
                .inflate(R.layout.table_item, null);

        EditTextMatrixCell instance = fl.findViewById(R.id.cell);

        instance.setBackground(getResources().getDrawable(supplier.get()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            instance.setShowSoftInputOnFocus(false);
        } else {
            instance.setTextIsSelectable(true);
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

    public interface TableChangeListener {
        void onTableChange();
    }

    private interface DrawableSupplier {
        Integer get();
    }
}
