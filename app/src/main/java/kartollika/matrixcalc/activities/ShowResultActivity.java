package kartollika.matrixcalc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;

import com.google.android.gms.ads.AdView;

import kartollika.matrixcalc.R;
import kartollika.matrixcalc.fragments.ShowResultFragment;
import kartollika.matrixmodules.operations.Operation;

public class ShowResultActivity extends SingleFragmentActivity {

    public static final String KEY_OPERATION_TO_SOLVE = "operation_to_solve";
    public static final String KEY_COEFFICIENT = "input_coefficient";

    private AdView adView;

    @Override
    protected Fragment createFragment() {
        Operation operation = (Operation) getIntent().getSerializableExtra(KEY_OPERATION_TO_SOLVE);
        return ShowResultFragment.newInstance(operation);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.show_result_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.ShowResultActivityThemeDark);
        }
        super.onCreate(savedInstanceState);

        adView = findViewById(R.id.adView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //AdUtils.destroyBanner(adView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_OK);
    }
}
