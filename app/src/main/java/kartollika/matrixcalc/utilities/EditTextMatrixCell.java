package kartollika.matrixcalc.utilities;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

public class EditTextMatrixCell extends android.support.v7.widget.AppCompatEditText {

    boolean hasMinus;
    boolean hasDivider;

    public EditTextMatrixCell(Context context) {
        super(context);
    }

    public EditTextMatrixCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextMatrixCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        super.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    public void switchMinus() {

    }

    public void setDivider() {

    }

}
