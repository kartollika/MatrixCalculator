package kartollika.matrixcalc.activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.Window;

import kartollika.matrixcalc.R;
import kartollika.matrixcalc.fragments.SplashScreenFragment;
import kartollika.matrixcalc.utilities.UpdateCheckerService;

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

        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean(PreferenceActivity.KEY_AUTOUPDATE, true)) {
            startService(UpdateCheckerService.getUpdateCheckerService(getApplicationContext()));
        }
    }
}
