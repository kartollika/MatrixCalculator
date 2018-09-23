package kartollika.matrixcalc.utilities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import kartollika.matrixcalc.R;

public class UpdateCheckerBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_UPDATE_CHECK_FINISHED
            = "kartollika.matrixcalc.UPDATE_CHECK_FINISHED";
    public static final String ACTION_UPDATE_CHECK_CONNECTION_ERROR =
            "kartollika.matrixcalc.UPDATE_CHECK_CONNECTION_ERROR";
    private boolean fromSettings = false;
    private UpdateNotifier notifier;

    public UpdateCheckerBroadcastReceiver(Context context) {
        notifier = new UpdateNotifier(context);
    }

    public void setActivity(Activity activity) {
        notifier = new UpdateNotifier(activity);
        fromSettings = true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) return;

        switch (intent.getAction()) {
            case ACTION_UPDATE_CHECK_FINISHED: {
                String newVersionName = intent.getStringExtra("new_version");
                if (newVersionName == null) {
                    if (fromSettings) {
                        notifier.sendNotification(intent.getAction(), context.getString(R.string.checkerservice_no_update_available), false);
                    }
                } else {
                    notifier.sendNotification(intent.getAction(), "Update " + newVersionName + " available", true);
                }
                break;
            }

            case ACTION_UPDATE_CHECK_CONNECTION_ERROR: {
                notifier.sendNotification(intent.getAction(), context.getString(R.string.checkerservice_connection_error), false);
            }
        }

    }
}
