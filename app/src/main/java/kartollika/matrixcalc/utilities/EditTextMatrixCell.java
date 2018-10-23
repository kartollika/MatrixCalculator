package kartollika.matrixcalc.utilities;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

public class EditTextMatrixCell extends androidx.appcompat.widget.AppCompatEditText {

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
                if (s.length() == 0) {
                    hasDivider = false;
                    hasMinus = false;
                    return;
                }

                hasMinus = s.charAt(0) == '-';

                String stringS = s.toString();
                hasDivider = stringS.contains(".") ^ stringS.contains("/");
            }
        });
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    public void switchMinus() {
        int selectionStart = getSelectionStart();
        String text;

        try {
            text = String.valueOf(getText());
        } catch (Exception e) {
            text = "";
        }


        if (text.length() == 0) {
            try {
                setText("-");
                setSelection(selectionStart + 1);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            char first = text.charAt(0);
            if (first == '-') {
                text = text.replaceFirst("-", "");
                setText(text);
                if (selectionStart != 0) {
                    setSelection(selectionStart - 1);
                }
            } else {
                text = "-" + text;
                setText(text);
                setSelection(selectionStart + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDivider() {
        int selectionStart = getSelectionStart();
        Editable text = getText();
        if (text == null) return;

        try {
            if (!hasDivider) {
                text.insert(selectionStart, ".");
                setText(text);
                setSelection(selectionStart + 1);
                return;
            }

            switchDivider(selectionStart, text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void switchDivider(int selectionStart, Editable text) {
        try {
            if (text.charAt(selectionStart - 1) == '.') {
                text.replace(selectionStart - 1, selectionStart, "/");
            } else if (text.charAt(selectionStart - 1) == '/') {
                text.replace(selectionStart - 1, selectionStart, ".");
            }

            setText(text);
            setSelection(selectionStart);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setHasMinus(boolean hasMinus) {
        this.hasMinus = hasMinus;
    }

    public void setHasDivider(boolean hasDivider) {
        this.hasDivider = hasDivider;
    }
}
