package kartollika.matrixcalc.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import kartollika.matrixcalc.App;
import kartollika.matrixcalc.IOperationSave;
import kartollika.matrixcalc.R;
import kartollika.matrixcalc.SolveCallback;
import kartollika.matrixcalc.fragments.AppRater;
import kartollika.matrixcalc.fragments.DefaultOperationsHubFragment;
import kartollika.matrixcalc.fragments.LinearSystemHubFragment;
import kartollika.matrixcalc.utilities.AdUtils;
import kartollika.matrixcalc.utilities.InterstitialShow;
import kartollika.matrixmodules.operations.Operation;

public class MainHubActivity extends SingleFragmentActivity implements SolveCallback, IOperationSave {

    private AdView adView;
    private Operation operation = Operation.NONE;

    @Override
    protected Fragment createFragment() {
        return DefaultOperationsHubFragment.newInstance(operation);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.main_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.MainHubActivityThemeDark);
        }

        if (savedInstanceState != null) {
            operation = (Operation) savedInstanceState.getSerializable(DefaultOperationsHubFragment.KEY_OPERATION_CHOSEN);
        }

        super.onCreate(savedInstanceState);

        adView = findViewById(R.id.adView);
        AdUtils.initBanner(this);
        AdUtils.initRewardedVideo(this);
        AdUtils.initInterstitialAd(this);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
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
                                .replace(R.id.fragment_container, DefaultOperationsHubFragment.newInstance(operation))
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
                if (!App.canShowNewBannersVideo() && !App.canShowNewInterstitialVideo()) {
                    Toasty.error(getApplicationContext(), getString(R.string.error_try_watch_ad_again),
                            Toast.LENGTH_SHORT).show();
                    return false;
                }

                final List<CharSequence> sequenceArrayList = new ArrayList<>();

                if (App.canShowNewBannersVideo()) {
                    sequenceArrayList.add(getString(R.string.block_ads,
                            getString(R.string.banners), App.BLOCKING_BANNERS));
                }

                if (App.canShowNewInterstitialVideo()) {
                    sequenceArrayList.add(getString(R.string.block_ads,
                            getString(R.string.interstitials), App.BLOCKING_INTERSITIALS));
                }

                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.block_ads_title)
                        .setItems(sequenceArrayList.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (sequenceArrayList.size() == 1) {
                                    if (App.canShowNewBannersVideo()) {
                                        showBannerVideo();
                                    } else {
                                        showInterstitialVideo();
                                    }
                                    return;
                                }
                                switch (i) {
                                    case 0:
                                        showBannerVideo();
                                        dialog.dismiss();
                                        break;

                                    case 1:
                                        showInterstitialVideo();
                                        dialog.dismiss();
                                }
                            }
                        })

                        .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })

                        .setCancelable(true)
                        .create();

                dialog.show();
                return true;
            }
        }
        return true;
    }

    private void showInterstitialVideo() {
        if (App.canShowNewInterstitialVideo()) {
            App.CUR_REWARD = "INTERSTITIAL";
            AdUtils.showRewardVideoAd(getApplicationContext());
        }
    }

    private void showBannerVideo() {
        if (App.canShowNewBannersVideo()) {
            App.CUR_REWARD = "BANNERS";
            AdUtils.showRewardVideoAd(getApplicationContext());
        }
    }

    private void openSettings(Context context) {
        Intent i = new Intent(context, PreferenceActivity.class);
        startActivity(i);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(DefaultOperationsHubFragment.KEY_OPERATION_CHOSEN, operation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdUtils.destroyBanner(adView);
        AdUtils.getRewardedVideoAd().destroy(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdUtils.getRewardedVideoAd().resume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AdUtils.getRewardedVideoAd().pause(this);
    }

    /**
     * OnSolve method of SolveCallback interface
     */
    @Override
    public void onSolve() {
        AppRater.shouldShowRater(this, getFragmentManager());
        InterstitialShow.showInterstitialAd();
    }

    @Override
    public void saveOperation(Operation operation) {
        this.operation = operation;
    }
}
