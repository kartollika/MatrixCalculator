package kartollika.matrixcalc.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import kartollika.matrixcalc.R;

public class AppRater extends DialogFragment {

    private final static int OPERATION_UNTIL_PROMPT = 5;
    private static final String PREF_NAME = "APP_RATER";
    private static final String DISABLED = "DISABLED";

    private static int operationsDone;

    public static void shouldShowRater(Context context, FragmentManager fragmentManager) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean canShow = false;

        operationsDone = sharedPreferences.getInt("operationsDone", 0);

        if (!sharedPreferences.getBoolean(DISABLED, false)) {
            if (++operationsDone % OPERATION_UNTIL_PROMPT == 0) {
                canShow = true;
            }
        }

        if (canShow) {
            new AppRater().show(fragmentManager, null);
        }

        editor.putInt("operationsDone", operationsDone);
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, 0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.apprater_title))
                .setMessage(getResources().getString(R.string.rate_big))
                .setPositiveButton(getResources().getString(R.string.rate), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=" + getActivity().getPackageName())));
                        } catch (ActivityNotFoundException e) {
                            Toasty.error(getActivity().getApplicationContext(),
                                    getString(R.string.gp_not_found), Toast.LENGTH_LONG).show();
                        }
                        getSharedPreferences(getActivity()).edit().putBoolean(DISABLED, true).apply();
                        dismiss();
                    }
                });
        builder.setNeutralButton(getResources().getString(R.string.apprater_remind_later), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });


        if (operationsDone / OPERATION_UNTIL_PROMPT >= 2) {
            builder.setNegativeButton(getResources().getString(R.string.apprater_not_show), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getSharedPreferences(getActivity()).edit().putBoolean(DISABLED, true).apply();
                    dismiss();
                }
            });
        }
        setCancelable(false);
        return builder.create();
    }
}