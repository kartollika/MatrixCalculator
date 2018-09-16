package kartollika.matrixcalc.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.Window;

import kartollika.matrixcalc.R;
import kartollika.matrixcalc.fragments.SplashScreenFragment;

public class SplashScreenActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return SplashScreenFragment.newInstance();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.SplashScreenActivityThemeDark);
        }
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
    }
}
