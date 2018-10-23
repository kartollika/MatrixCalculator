package kartollika.matrixcalc.activities;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.Serializable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import kartollika.matrixcalc.R;
import kartollika.matrixcalc.fragments.ShowResultFragment;
import kartollika.matrixcalc.utilities.AdUtils;
import kartollika.matrixmodules.operations.Operation;

public class ShowResultActivity extends SingleFragmentActivity {

    public static final String KEY_OPERATION_TO_SOLVE = "operation_to_solve";
    public static final String EVENT_SHOW_RESULT = "show_result";
    public static final String PARAM_OPERATION_CHOSEN = "chosen_operation";
    public static final String KEY_COEFFICIENT = "input_coefficient";
    public static final String TAG = "ShowResultActivity";

    private FirebaseAnalytics firebaseAnalytics;

    private AdView adView;

    @Override
    protected Fragment createFragment() {
        Operation operation = (Operation) getIntent().getSerializableExtra(KEY_OPERATION_TO_SOLVE);
        sendAnalyticsChosenOperation(operation);
        Serializable number = getIntent().getSerializableExtra(KEY_COEFFICIENT);
        if (number == null) {
            return ShowResultFragment.newInstance(operation);
        } else {
            return ShowResultFragment.newInstance(operation, (Number) number);
        }
    }

    private void sendAnalyticsChosenOperation(Operation operation) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM_OPERATION_CHOSEN, operation.toString());
        firebaseAnalytics.logEvent(EVENT_SHOW_RESULT, bundle);
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

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        super.onCreate(savedInstanceState);

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
