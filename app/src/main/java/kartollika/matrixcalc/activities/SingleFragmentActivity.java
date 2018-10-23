package kartollika.matrixcalc.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import kartollika.matrixcalc.R;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    protected abstract int getLayoutView();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (!isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onCreate(savedInstanceState);
        setContentView(getLayoutView());

        FragmentManager fm = getSupportFragmentManager();
        Fragment currFragment = fm.findFragmentById(R.id.fragment_container);
        if (currFragment == null) {
            currFragment = createFragment();
            fm.beginTransaction()
                    .replace(R.id.fragment_container, currFragment)
                    .commit();
        }
    }
}
