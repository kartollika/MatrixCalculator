package kartollika.matrixcalc.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import kartollika.matrixcalc.R;
import kartollika.matrixcalc.fragments.SplashScreenFragment;
import kartollika.matrixcalc.utilities.UpdateCheckerService;

public class SplashScreenActivity extends SingleFragmentActivity {

    private static final String TAG = "SplashScreenActivity";
    private static final int SPLASH_SCREEN_TIMEOUT = 1500;

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
        Log.i(TAG, "onCreate: " + TextUtils.isEmpty(""));
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.SplashScreenActivityThemeDark);
        }
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        try {
            getSupportActionBar().hide();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean(PreferenceActivity.KEY_AUTOUPDATE, true)) {
            startService(UpdateCheckerService.getUpdateCheckerService(getApplicationContext()));
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMainActivity(getApplicationContext());
            }
        }, SPLASH_SCREEN_TIMEOUT);
    }

    private void loadMainActivity(Context context) {
        Intent i = new Intent(context, MainHubActivity.class);
        startActivity(i);
        finish();
    }
}
