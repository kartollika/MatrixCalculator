package kartollika.matrixcalc.utilities;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import kartollika.matrixcalc.App;
import kartollika.matrixcalc.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class UpdateNotifier {

    private static final String UPDATE_TAG = "update_matrixcalc";
    private static final String NOTIFICATION_CHANNEL_ID = "kartollika.matrixcalc_CHANNEL";

    private Activity activity;
    private Context context;

    UpdateNotifier(@NonNull Context context) {
        this.context = context;
    }

    UpdateNotifier(@NonNull Activity activity) {
        context = activity.getApplicationContext();
        this.activity = activity;
    }


    void sendNotification(@NonNull String status, @NonNull String message, boolean hasUpdates) {
        final Snackbar snackbar;
        if (status.equals(UpdateCheckerBroadcastReceiver.ACTION_UPDATE_CHECK_CONNECTION_ERROR)) {
            try {
                snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);

                snackbar.setAction(R.string.try_again, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.startService(UpdateCheckerService.getUpdateCheckerService(activity));
                        snackbar.dismiss();
                    }
                }).show();
            } catch (Exception e) {
                if (activity != null) {
                    Toasty.error(context, message, Toast.LENGTH_LONG).show();
                }
            }
        } else {
            if (hasUpdates) {
                try {
                    snackbar = Snackbar.make(activity.findViewById(android.R.id.content),
                            message, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Update", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            App.openGooglePlay(activity);
                            NotificationManager notificationManager =
                                    (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                            if (notificationManager != null) {
                                notificationManager.cancel(0);
                            }
                            snackbar.dismiss();
                        }
                    }).show();

                    sendNotificationInPanel(message);
                } catch (Exception e) {
                    Toasty.info(context, context.getString(R.string.checkerservice_click_notification_to_proceed), Toast.LENGTH_LONG).show();
                    sendNotificationInPanel(message);
                }
            } else {
                try {
                    snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(R.string.snackbar_good, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    }).show();
                } catch (Exception e) {
                    Toasty.info(context, message, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void sendNotificationInPanel(String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            Toasty.warning(context, context.getString(R.string.update_via_google_play), Toast.LENGTH_LONG).show();
            return;
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "kartollika.matrixcalc.channel", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName()));
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        notificationBuilder.setSmallIcon(R.mipmap.icon);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setContentTitle(context.getString(R.string.checkerservice_update_available));
        notificationBuilder.setOngoing(false);
        notificationBuilder.setContentText(message);
        notificationBuilder.setContentIntent(pIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(context.getResources().getColor(R.color.colorPrimary));
        }
        notificationManager.notify(0, notificationBuilder.build());
    }
}
