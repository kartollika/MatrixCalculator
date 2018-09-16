package kartollika.matrixcalc.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import kartollika.matrixcalc.BuildConfig;
import kartollika.matrixcalc.R;

public class UpdateCheckerService extends Service {

    private static final String CHECK_UPDATE_URL = "https://rawgit.com/kartollika/Matrix_calculator-Android/master/app/latest_version_new.txt";
    private static final String TAG = "UpdateCheckerService";
    private static final int TIMEOUT_GETTER = 5000;
    private NotificationManager notificationManager;
    private boolean notifsAtStartAllowed;
    private boolean calledFromSettings;

    public static Intent getUpdateCheckerService(Context context, boolean fromSettings) {
        Intent intent = new Intent(context, UpdateCheckerService.class);
        intent.putExtra("fromSettings", fromSettings);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        try {
            notifsAtStartAllowed = intent.getExtras().getBoolean("notifsAtStartAllowed");
        } catch (Exception ignored) {
        }
        try {
            calledFromSettings = intent.getExtras().getBoolean("fromSettings", false);
        } catch (Exception ignored) {
        }

        if ((notifsAtStartAllowed && !calledFromSettings) ^ calledFromSettings) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    checkForUpdates();
                }
            }).start();
        }

        return Service.START_NOT_STICKY;
    }

    private void showToast(final Context context, final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    private void checkForUpdates() {
        if (hasConnection()) {
            int curVersionCode = BuildConfig.VERSION_CODE;
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            Future<String> future = executorService.submit(new DownloadCallable(CHECK_UPDATE_URL));

            String versionLineReady = null;
            try {
                versionLineReady = future.get(TIMEOUT_GETTER, TimeUnit.MILLISECONDS);
                Log.i(TAG, versionLineReady);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

            if (versionLineReady == null) {
                stopSelf();
                return;
            }

            Object[] parts = versionLineReady.split(" ");
            Pair<Integer, String> latestVersion = new Pair<>(Integer.parseInt((String) parts[0]), (String) parts[1]);


            if (curVersionCode < latestVersion.first) {
                if (calledFromSettings) {
                    Intent i = new Intent();
                    i.setAction("kartollika.matrixcalc.UPDATE_CHECK_FINISHED");
                    i.putExtra("is_update_available", true);
                    i.putExtra("new_version", latestVersion.second);
                    sendBroadcast(i);
                    stopSelf();
                    return;
                }
                if (notifsAtStartAllowed) {
                    sendNotification(latestVersion.second);
                }

            } else {
                Intent i = new Intent();
                i.setAction("kartollika.matrixcalc.UPDATE_CHECK_FINISHED");
                i.putExtra("is_update_available", false);
                sendBroadcast(i);
            }
        } else {
            showToast(getApplicationContext(), getResources().getString(R.string.conn_error));
        }
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void sendNotification(String latestVersionName) {
        Notification.Builder notifBuilder = new Notification.Builder(getApplicationContext());

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        String contentText = getString(R.string.clickToUpdate, latestVersionName);

        notifBuilder.setSmallIcon(R.mipmap.icon);
        notifBuilder.setAutoCancel(true);
        notifBuilder.setContentTitle(getString(R.string.update_available));
        notifBuilder.setOngoing(false);
        notifBuilder.setContentText(contentText);
        notifBuilder.setContentIntent(pIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notifBuilder.setColor(Color.LTGRAY);
        }
        notificationManager.notify(0, notifBuilder.build());
    }

    private class DownloadCallable implements Callable<String> {

        private String URL;

        DownloadCallable(String URL) {
            this.URL = URL;
        }

        @Override
        public String call() throws Exception {
            String versionLine = null;
            BufferedReader in = null;

            try {
                URL url = new URL(URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(30000);
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                versionLine = in.readLine();
            } finally {
                if (in != null) {
                    in.close();
                }
            }
            return versionLine;
        }

    }
}
