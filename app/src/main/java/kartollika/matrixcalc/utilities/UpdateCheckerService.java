package kartollika.matrixcalc.utilities;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

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

public class UpdateCheckerService extends Service {

    private static final String CHECK_UPDATE_URL = "https://rawgit.com/kartollika/Matrix_calculator-Android/master/app/latest_version_new.txt";
    private static final String TAG = "UpdateCheckerService";
    private static final int TIMEOUT_GETTER = 5000;

    public static Intent getUpdateCheckerService(Context context) {
        Intent intent = new Intent(context, UpdateCheckerService.class);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkForUpdates();
            }
        }).start();

        return Service.START_NOT_STICKY;
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
                Intent i = new Intent();
                i.setAction("kartollika.matrixcalc.UPDATE_CHECK_FINISHED");
                i.putExtra("is_update_available", true);
                i.putExtra("new_version", latestVersion.second);
                sendBroadcast(i);
                stopSelf();
                return;
            } else {
                Intent i = new Intent();
                i.setAction(UpdateCheckerBroadcastReceiver.ACTION_UPDATE_CHECK_FINISHED);
                i.putExtra("is_update_available", false);
                sendBroadcast(i);
                stopSelf();
                return;
            }
        } else {
            Intent i = new Intent();
            i.setAction(UpdateCheckerBroadcastReceiver.ACTION_UPDATE_CHECK_CONNECTION_ERROR);
            sendBroadcast(i);
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

    private class DownloadCallable implements Callable<String> {

        private String URL;

        DownloadCallable(String URL) {
            this.URL = URL;
        }

        @Override
        public String call() throws Exception {
            String versionLine;
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
