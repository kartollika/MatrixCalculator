package kartollika.matrixcalc;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;

import kartollika.matrixcalc.utilities.InterstitialShow;

public class App extends Application {

    // public static Matrix[] matrices = new Matrix[2];
    // public static Matrix systemMatrix;
    public static final String TARGET_DEVICE_ID = "8161507EB49B3F33630CF2A74D743868";
    public static final int BLOCKING_BANNERS = 90;
    public static final int BLOCKING_INTERSITIALS = 60;
    public static final String email = "maksimow.dmitrij@yandex.ru";
    private static final String APP_ID = "ca-app-pub-9193176037122415~9633966613";
    public static String version;
    public static String CUR_REWARD;
    private static long estimatedTimeRemovingBanners;
    private static long estimatedTimeRemovingInterstitial;
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
        return "Device Info:" + "\n OS Version: " +
                System.getProperty("os.version") + "(" + Build.VERSION.CODENAME + ")" +
                "\n App version/build: " + BuildConfig.VERSION_NAME + "/" + BuildConfig.VERSION_CODE +
                "\n OS API Level: " + Build.VERSION.SDK_INT +
                "\n Device: " + Build.DEVICE +
                "\n Model: " + Build.MODEL + " (" + Build.PRODUCT + ")" +
                "\n Resolution " + metrics.widthPixels
                + "x" + metrics.heightPixels;
    }

    public static void writeEmail(Context context) {
        Resources resources = context.getResources();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent = Intent.createChooser(emailIntent, "Choose the client for sending email..");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{App.email});
       /* emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                resources.getString(R.string.email_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT,
                resources.getString(R.string.email_text) +
                        "\n\n\n==========================\n" +
                        App.getDeviceInfo(activity.getWindowManager()));*/
        context.startActivity(Intent.createChooser(emailIntent, "Sending email..."));
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

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.BUILD_TYPE == "debug") {
            version = BuildConfig.VERSION_NAME + "_" + BuildConfig.BUILD_TYPE;
        } else {
            version = BuildConfig.VERSION_NAME;
        }

        MobileAds.initialize(getApplicationContext(), APP_ID);
        //    AdUtils.initResources(this);


        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        AppCompatDelegate.setDefaultNightMode(preferences.getBoolean("isDarkmode", false)
                ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        InterstitialShow.CUR_OPERATIONS = preferences.getInt("interstitialCurOperations", 1);
        setEstimatedTimeBanners(preferences.getLong("bannersEstimatedTime", System.currentTimeMillis()));
        setEstimatedTimeInterstitial(preferences.getLong("interstitialEstimatedTime", System.currentTimeMillis()));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            editor.putBoolean("isDarkmode", true);
        } else {
            editor.putBoolean("isDarkmode", false);
        }
        editor.apply();
    }
}
