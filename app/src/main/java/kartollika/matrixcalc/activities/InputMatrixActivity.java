package kartollika.matrixcalc.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.Window;

import kartollika.matrixcalc.R;
import kartollika.matrixcalc.fragments.InputMatrixFragment;

public class InputMatrixActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return InputMatrixFragment.newInstance();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.InputMatrixActivityThemeDark);
        }

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            // Ignoring
        }
        super.onCreate(savedInstanceState);
    }
}
