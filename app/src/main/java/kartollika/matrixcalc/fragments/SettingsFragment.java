package kartollika.matrixcalc.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import kartollika.matrixcalc.App;
import kartollika.matrixcalc.BuildConfig;
import kartollika.matrixcalc.R;
import kartollika.matrixcalc.utilities.UpdateCheckerService;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "SettingsFragment";

    Context context;

    private SharedPreferences sharedPreferences;
    private BroadcastReceiver updateCheckerBroadcastReceiver;

    private Preference defaultRowsPreference;
    private Preference defaultColumnsPreference;
    private SwitchPreference nightmodePreference;
    private CheckBoxPreference experimentalPreference;
    private CheckBoxPreference checkAutoUpdatePreference;
    private Preference versionPreference;
    private Preference sendReportPreference;
    private Preference checkManuallyUpdatePreference;
    private Preference openGooglePlayPreference;

    public SettingsFragment() {

    }

    public static SettingsFragment newInstance() {

        Bundle args = new Bundle();

        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        defaultRowsPreference = findPreference("default_rows");
        defaultColumnsPreference = findPreference("default_columns");
        nightmodePreference = (SwitchPreference) findPreference("nightmode");
        checkAutoUpdatePreference = (CheckBoxPreference) findPreference("auto_update");
        experimentalPreference = (CheckBoxPreference) findPreference("experimental");
        versionPreference = findPreference("app_version");
        sendReportPreference = findPreference("send_report");
        checkManuallyUpdatePreference = findPreference("check_updates");
        openGooglePlayPreference = findPreference("open_playmarket");

        bindOnClickPreferences();

        Log.i(TAG, "onCreate called");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private void bindOnClickPreferences() {
        experimentalPreference.setChecked(sharedPreferences.getBoolean("experimental", false));
        experimentalPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                return false;
            }
        });

        nightmodePreference.setChecked(sharedPreferences.getBoolean("nightmode", false));
        nightmodePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if ((boolean) o) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                try {
                    requireActivity().recreate();
                } catch (Exception e) {
                    // Ignoring
                }
                return true;
            }
        });

        versionPreference.setSummary(BuildConfig.VERSION_NAME + " (build " + BuildConfig.VERSION_CODE + ")");
        versionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return true;
            }
        });

        sendReportPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                App.writeEmail(requireContext());
                return false;
            }
        });

        checkManuallyUpdatePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                context.startService(UpdateCheckerService.getUpdateCheckerService(getActivity(), true));
                return true;
            }
        });

        openGooglePlayPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                App.openGooglePlay(context);
                return true;
            }
        });
    }

    private void sendUpdateNotificationAsSnackbar(String newVersionName) throws IllegalStateException {
        final Snackbar updateSnackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content),
                "Update to version " + newVersionName + " available!", Snackbar.LENGTH_INDEFINITE);
        updateSnackbar.setAction("Update now!", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSnackbar.dismiss();
                App.openGooglePlay(context);
            }
        });
        updateSnackbar.show();
    }

    private void sendUpdateNotification(String newVersionName) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.preference_screen, s);
        Log.i(TAG, "onCreatePreferences called");
    }

    private void notifyUserAboutUpdate(String newVersionName) {
        try {
            sendUpdateNotificationAsSnackbar(newVersionName);
        } catch (IllegalStateException ise) {
            sendUpdateNotification(newVersionName);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateCheckerBroadcastReceiver = new UpdateCheckerBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("kartollika.matrixcalc.UPDATE_CHECK_FINISHED");
        context.registerReceiver(updateCheckerBroadcastReceiver, intentFilter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(updateCheckerBroadcastReceiver);
        updateCheckerBroadcastReceiver = null;
    }

    private class UpdateCheckerBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isUpdateAvailable = intent.getBooleanExtra("is_update_available", false);
            if (isUpdateAvailable) {
                String newVersionName = intent.getStringExtra("new_version");
                notifyUserAboutUpdate(newVersionName);
            } else {
                Toast.makeText(requireContext(), "Great news! You are up to date!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
