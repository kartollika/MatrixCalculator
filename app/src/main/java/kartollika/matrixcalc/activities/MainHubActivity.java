package kartollika.matrixcalc.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.ads.AdView;

import kartollika.matrixcalc.App;
import kartollika.matrixcalc.IOperationChoose;
import kartollika.matrixcalc.R;
import kartollika.matrixcalc.SolveCallback;
import kartollika.matrixcalc.fragments.DefaultOperationsHubFragment;
import kartollika.matrixcalc.fragments.LinearSystemHubFragment;
import kartollika.matrixcalc.utilities.AdUtils;

import static kartollika.matrixcalc.utilities.AdUtils.rewardedVideoAd;

public class MainHubActivity extends SingleFragmentActivity implements SolveCallback, IOperationChoose {

    private AdView adView;
    private BottomNavigationView bottomNavigationView;
    private int isNightmodeOn = AppCompatDelegate.MODE_NIGHT_NO;

    @Override
    protected Fragment createFragment() {
        return DefaultOperationsHubFragment.newInstance();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.main_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.MainHubActivityThemeDark);
            isNightmodeOn = AppCompatDelegate.MODE_NIGHT_YES;
        }

        super.onCreate(savedInstanceState);

        adView = findViewById(R.id.adView);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = fm.findFragmentById(R.id.fragment_container);
                switch (item.getItemId()) {
                    case R.id.navigation_operations: {
                        if (fragment != null &&
                                fragment.getClass().getSimpleName()
                                        .equals(DefaultOperationsHubFragment.TAG)) {
                            break;
                        }
                        fm.beginTransaction()
                                .replace(R.id.fragment_container, DefaultOperationsHubFragment.newInstance())
                                .commit();
                        break;
                    }


                    case R.id.navigation_linearsystem: {
                        if (fragment != null &&
                                fragment.getClass().getSimpleName()
                                        .equals(LinearSystemHubFragment.TAG)) {
                            break;
                        }

                        fm.beginTransaction()
                                .replace(R.id.fragment_container, LinearSystemHubFragment.newInstance())
                                .commit();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mainhub_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_item_settings: {
                openSettings(this);
                break;
            }

            case R.id.menu_item_send_email: {
                App.writeEmail(this);
                break;
            }

            case R.id.menu_item_rate_in_gp: {
                App.openGooglePlay(this);
                break;
            }

            case R.id.menu_item_block_ads: {
            }
        }
        return true;
    }

    private void openSettings(Context context) {
        Intent i = new Intent(context, PreferenceActivity.class);
        startActivity(i);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("isNightmodeOn", isNightmodeOn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdUtils.destroyBanner(adView);
        //rewardedVideoAd.destroy(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //rewardedVideoAd.resume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //rewardedVideoAd.pause(this);
    }

    /**
     * OnSolve method of SolveCallback interface
     */
    @Override
    public void onSolve() {

    }

    @Override
    public void onOperationChoosed() {

    }
}
