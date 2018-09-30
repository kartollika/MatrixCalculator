package kartollika.matrixcalc.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.google.android.gms.ads.AdView;

import java.io.Serializable;

import kartollika.matrixcalc.R;
import kartollika.matrixcalc.fragments.ShowResultFragment;
import kartollika.matrixcalc.utilities.AdUtils;
import kartollika.matrixmodules.operations.Operation;

public class ShowResultActivity extends AppCompatActivity {

    public static final String KEY_OPERATION_TO_SOLVE = "operation_to_solve";
    public static final String KEY_COEFFICIENT = "input_coefficient";

    private AdView adView;

    // @Override
    protected Fragment createFragment() {
        Operation operation = (Operation) getIntent().getSerializableExtra(KEY_OPERATION_TO_SOLVE);
        Serializable number = getIntent().getSerializableExtra(KEY_COEFFICIENT);
        if (number == null) {
            return ShowResultFragment.newInstance(operation);
        } else {
            return ShowResultFragment.newInstance(operation, (Number) number);
        }
    }

    // @Override
    protected int getLayoutView() {
        return R.layout.show_result_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.ShowResultActivityThemeDark);
        }

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        //if (!isTablet) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // }

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
        //super.onCreate(savedInstanceState);

        adView = findViewById(R.id.adView);
        AdUtils.initBanner(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdUtils.destroyBanner(adView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_OK);
        finish();
    }
}
