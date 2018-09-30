package kartollika.matrixcalc.utilities;

import android.content.Context;

import kartollika.matrixcalc.R;
import kartollika.matrixmodules.OperationHints;

public class OperationHintsAndroid extends OperationHints {

    private Context context;

    public OperationHintsAndroid(Context context) {
        super();
        this.context = context;
    }

    @Override
    public String hintMultiplyLineByNumber(int line, Number coefficient) {
        return context.getString(R.string.multiply_line, line + 1, coefficient);
    }

    @Override
    public String hintSwapLines(int line1, int line2) {
        return context.getString(R.string.swap_lines, line1 + 1, line2 + 1);
    }

    @Override
    public String hintSubtractLineFromLineMultipliedByNumber(int lineToSubtractFrom,
                                                             int lineToSubtract, Number coefficient) {
        return context.getString(R.string.subtract_line_from_multiplied_line,
                lineToSubtractFrom + 1, lineToSubtract + 1, coefficient);
    }

    @Override
    public String hintDeterminantGetResult(int swaps) {
        return context.getString(R.string.multiply_diagonal_elements, swaps);
    }

    @Override
    public String hintSystemHasSolves() {
        return context.getString(R.string.system_has_solves);
    }

    @Override
    public String hintSystemHasNotSolves(int i, Number coefficient) {
        return context.getString(R.string.system_has_no_solves, i + 1, coefficient);
    }

    @Override
    public String hintInverseMatrix() {
        return context.getString(R.string.inverse_result);
    }

    @Override
    public String scrollDownToSeeMore() {
        return context.getString(R.string.scroll_to_see_more);
    }

    @Override
    public String hintDeterminantResult() {
        return context.getString(R.string.determinant_result);
    }

    @Override
    public String transformToDiagonal() {
        return context.getString(R.string.steps_triangle_start);
    }

    @Override
    public String transformToUpperTriangle() {
        return context.getString(R.string.steps_diagonal_start);
    }
}
