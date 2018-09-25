package kartollika.matrixcalc.utilities;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;

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

            }

            /** SWITCH SIGN */
            case 56: {

                break;
            }

            /** DIVIDER */
            case 69: {

                break;
            }

            /** DELETE */
            case 67: {
                keyEvent = new KeyEvent(l, l, 0, primaryCode, 0, 0, 0, 0, 6);
                activityWeakReference.get().dispatchKeyEvent(keyEvent);
                break;
            }

            /** OTHER */
            default:
                keyEvent = new KeyEvent(l, l, 0, primaryCode, 0, 0, 0, 0, 6);
                activityWeakReference.get().dispatchKeyEvent(keyEvent);
        }

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
