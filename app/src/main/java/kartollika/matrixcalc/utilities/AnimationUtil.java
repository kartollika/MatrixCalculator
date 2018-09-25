package kartollika.matrixcalc.utilities;

import android.support.constraint.Group;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

public class AnimationUtil {

    public static boolean switchFullCardVisibility(final ViewGroup root, ViewGroup group) {
        boolean isInvisible = group.getVisibility() != View.VISIBLE;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Transition transition = null;
            transition = new ChangeBounds();

            transition.setInterpolator(new OvershootInterpolator());
            if (!isInvisible) {
                transition.setDuration(300);
                transition.setInterpolator(new OvershootInterpolator());
            }

            TransitionManager.beginDelayedTransition(root, transition);
            group.setVisibility(isInvisible ? View.VISIBLE : View.GONE);

            return !isInvisible;
        }

        group.setVisibility(isInvisible ? View.VISIBLE : View.GONE);
        return !isInvisible;
    }

    public static boolean switchControlCardVisibility(ViewGroup root, final Group group) {
        boolean isInvisible = group.getVisibility() != View.VISIBLE;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Transition transition;

            transition = new ChangeBounds();

            transition.setInterpolator(new OvershootInterpolator());
            TransitionManager.beginDelayedTransition(root, transition);
            if (isInvisible) {
                transition.setDuration(400);
            }
            group.setVisibility(isInvisible ? View.VISIBLE : View.GONE);

            return !isInvisible;
        }

        group.setVisibility(isInvisible ? View.VISIBLE : View.GONE);
        return !isInvisible;


    }
}
