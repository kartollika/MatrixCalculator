package kartollika.matrixcalc.utilities;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;

import java.lang.ref.WeakReference;

public class KeyboardKeyListener implements KeyboardView.OnKeyboardActionListener {

    private WeakReference<Activity> activityWeakReference;

    public KeyboardKeyListener(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        long l = System.currentTimeMillis();
        EditTextMatrixCell et;
        KeyEvent keyEvent;

        switch (primaryCode) {
            case Keyboard.KEYCODE_DONE: {
                try {
                    et = (EditTextMatrixCell) activityWeakReference.get().getCurrentFocus();
                    if (et == null) {
                        throw new Exception("no view found");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                try {
                    activityWeakReference.get().findViewById(et.getNextFocusForwardId()).requestFocus();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            }

            /** SWITCH SIGN */
            case 69: {
                try {
                    EditTextMatrixCell editTextMatrixCell = (EditTextMatrixCell) getEditTextMatrixCellByFocus();
                    editTextMatrixCell.switchMinus();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                break;
            }

            /** DIVIDER */
            case 56: {
                try {
                    EditTextMatrixCell editTextMatrixCell = (EditTextMatrixCell) getEditTextMatrixCellByFocus();
                    editTextMatrixCell.setDivider();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                break;
            }

            /** DELETE */
            case 67:
            default: {
                keyEvent = new KeyEvent(l, l, 0, primaryCode, 0, 0, 0, 0, 6);
                activityWeakReference.get().dispatchKeyEvent(keyEvent);
                break;
            }
        }
    }

    private View getEditTextMatrixCellByFocus() {
        EditTextMatrixCell et;
        try {
            et = (EditTextMatrixCell) activityWeakReference.get().getCurrentFocus();
            if (et == null) {
                throw new NullPointerException("no view found");
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("View with focus has other class");
        }

        return et;
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
