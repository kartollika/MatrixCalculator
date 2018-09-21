package kartollika.matrixcalc;

import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;

import kartollika.matrixcalc.utilities.AdUtils;
import kartollika.matrixcalc.utilities.InterstitialShow;
import kartollika.matrixcalc.utilities.UpdateCheckerBroadcastReceiver;

public class App extends Application {

    public static final String TARGET_DEVICE_ID = "8161507EB49B3F33630CF2A74D743868";
    public static final int BLOCKING_BANNERS = 90;
    public static final int BLOCKING_INTERSITIALS = 60;
    public static final String email = "maksimow.dmitrij@yandex.ru";
    private static final String TAG = "APP";
    private static final String APP_ID = "ca-app-pub-9193176037122415~9633966613";
    public static String version;
    public static String CUR_REWARD;
    private static long estimatedTimeRemovingBanners;
    private static long estimatedTimeRemovingInterstitial;
    //private MatrixManager matrixManager;
    private static UpdateCheckerBroadcastReceiver updateCheckerBroadcastReceiver;
    public boolean thisSession = false;
    private SharedPreferences preferences;

    public static void setEstimatedTimeBanners(long time) {
        estimatedTimeRemovingBanners = time;
    }

    public static long getEstimatedTimeRemovingBanners() {
        return estimatedTimeRemovingBanners;
    }

    public static long getEstimatedTimeRemovingInterstitial() {
        return estimatedTimeRemovingInterstitial;
    }

    public static void setEstimatedTimeInterstitial(long time) {
        estimatedTimeRemovingInterstitial = time;
    }

    public static boolean canShowNewBannersVideo() {
        return estimatedTimeRemovingBanners <= System.currentTimeMillis();
    }

    public static boolean canShowNewInterstitialVideo() {
        return estimatedTimeRemovingInterstitial <= System.currentTimeMillis();
    }

    public static String getDeviceInfo(WindowManager windowManager) {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return "Device Info:" +
                "\n App version/build: " + BuildConfig.VERSION_NAME + "/" + BuildConfig.VERSION_CODE +
                "\n OS API Level: " + Build.VERSION.SDK_INT +
                "\n Device: " + Build.DEVICE +
                "\n Model: " + Build.MODEL + " (" + Build.PRODUCT + ")" +
                "\n Resolution " + metrics.widthPixels
                + "x" + metrics.heightPixels;
    }

    public static void writeEmail(Activity activity) {
        Resources resources = activity.getResources();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent = Intent.createChooser(emailIntent, "Choose the client for sending email..");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{App.email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                resources.getString(R.string.email_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT,
                resources.getString(R.string.email_text) +
                        "\n\n\n==========================\n" +
                        App.getDeviceInfo(activity.getWindowManager()));
        activity.startActivity(Intent.createChooser(emailIntent, "Sending email..."));
    }

    public static void openGooglePlay(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        intent.setData(uri);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(context, R.string.gp_not_found, Toast.LENGTH_LONG).show();
        }
    }

    public static void initUpdaterBroadcastReceiver(Context context) {
        updateCheckerBroadcastReceiver = new UpdateCheckerBroadcastReceiver(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UpdateCheckerBroadcastReceiver.ACTION_UPDATE_CHECK_FINISHED);
        intentFilter.addAction(UpdateCheckerBroadcastReceiver.ACTION_UPDATE_CHECK_CONNECTION_ERROR);
        context.registerReceiver(updateCheckerBroadcastReceiver, intentFilter);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        /*AppCompatDelegate.setDefaultNightMode(preferences.getBoolean("isDarkmode", false)
                ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);*/

        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            version = BuildConfig.VERSION_NAME + "_" + BuildConfig.BUILD_TYPE;
        } else {
            version = BuildConfig.VERSION_NAME;
        }

        initUpdaterBroadcastReceiver(getApplicationContext());
        //initAds();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "onTerminate called");
        unregisterReceiver(updateCheckerBroadcastReceiver);
    }

    private void initAds() {
        MobileAds.initialize(getApplicationContext(), APP_ID);
        AdUtils.initResources(this);
        InterstitialShow.CUR_OPERATIONS = preferences.getInt("interstitialCurOperations", 1);
        setEstimatedTimeBanners(preferences.getLong("bannersEstimatedTime", System.currentTimeMillis()));
        setEstimatedTimeInterstitial(preferences.getLong("interstitialEstimatedTime", System.currentTimeMillis()));
    }

    public static UpdateCheckerBroadcastReceiver getUpdateCheckerBroadcastReceiver() {
        return updateCheckerBroadcastReceiver;
    }
}
