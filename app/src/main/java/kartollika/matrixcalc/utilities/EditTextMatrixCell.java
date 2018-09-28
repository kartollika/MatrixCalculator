package kartollika.matrixcalc.utilities;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

public class EditTextMatrixCell extends android.support.v7.widget.AppCompatEditText {

    public static final String TAG = "EditTextMatrixCell";

    private boolean hasMinus;
    private boolean hasDivider;

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
        String text = String.valueOf(getText());
        if (text.length() == 0) {
            setText("-");
            hasMinus = true;
        }
        Character first = text.charAt(0);
        if (first.compareTo('-') == 0) {
            text = text.replaceFirst("-", "");
        } else {
            text = "-" + text;
        }
        setText(text);
        hasMinus = !hasDivider;
    }

    public void setDivider(int currentFocus) {
        String text = String.valueOf(getText());
        if (text.length() == currentFocus) {
            //switchDivider();
            return;
        }

    }

    private void switchDivider(String text) {
        if (!hasDivider) {
            text = text + ".";
            setText(text);
            return;
        }
        text = text.replace(".", "/");
        setText(text);
    }

    public void setHasMinus(boolean hasMinus) {
        this.hasMinus = hasMinus;
    }

    public void setHasDivider(boolean hasDivider) {
        this.hasDivider = hasDivider;
    }
}
