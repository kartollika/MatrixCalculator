package kartollika.matrixcalc.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.android.gms.ads.AdView;

import kartollika.matrixcalc.R;
import kartollika.matrixcalc.fragments.ShowResultFragment;
import kartollika.matrixcalc.utilities.AdUtils;

public class ShowResultActivity extends SingleFragmentActivity {

    private AdView adView;

    @Override
    protected Fragment createFragment() {
        return ShowResultFragment.newInstance();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //AdUtils.destroyBanner(adView);
    }
}
