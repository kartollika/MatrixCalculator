package kartollika.matrixcalc.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;

import kartollika.matrixcalc.App;
import kartollika.matrixcalc.BuildConfig;
import kartollika.matrixcalc.R;
import kartollika.matrixcalc.utilities.UpdateCheckerService;

public class PreferenceActivity extends android.preference.PreferenceActivity {
    public static final String TAG = "PreferenceActivity";

    public static final String KEY_DEFAULT_ROWS = "default_rows";
    public static final String KEY_DEFAULT_COLUMNS = "default_columns";
    public static final String KEY_EXPERIMENTAL = "experimental";
    public static final String KEY_NIGHTMODE = "nightmode";
    public static final String KEY_AUTOUPDATE = "auto_update";
    public static final String KEY_APP_VERSION = "app_version";
    public static final String KEY_CHECK_UPDATES = "check_updates";
    public static final String KEY_SEND_REPORT = "send_report";
    public static final String KEY_OPEN_GOOGLE_PLAY = "open_playmarket";

    private static final String REQUEST_SET_DEFAULT_ROWS = "set_default_rows";
    private static final String REQUEST_SET_DEFAULT_COLUMNS = "set_default_columns";
    private SharedPreferences sharedPreferences;

    private Preference defaultRowsPreference;
    private Preference defaultColumnsPreference;
    private SwitchPreference nightmodePreference;
    private CheckBoxPreference experimentalPreference;
    private CheckBoxPreference checkAutoUpdatePreference;
    private Preference versionPreference;
    private Preference sendReportPreference;
    private Preference checkManuallyUpdatePreference;
    private Preference openGooglePlayPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.SettingsActivityThemeDark);
        }

        App.getUpdateCheckerBroadcastReceiver().setActivity(PreferenceActivity.this);

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_screen);

        defaultRowsPreference = findPreference(KEY_DEFAULT_ROWS);
        defaultColumnsPreference = findPreference(KEY_DEFAULT_COLUMNS);
        nightmodePreference = (SwitchPreference) findPreference(KEY_NIGHTMODE);
        checkAutoUpdatePreference = (CheckBoxPreference) findPreference(KEY_AUTOUPDATE);
        experimentalPreference = (CheckBoxPreference) findPreference(KEY_EXPERIMENTAL);
        versionPreference = findPreference(KEY_APP_VERSION);
        sendReportPreference = findPreference(KEY_SEND_REPORT);
        checkManuallyUpdatePreference = findPreference(KEY_CHECK_UPDATES);
        openGooglePlayPreference = findPreference(KEY_OPEN_GOOGLE_PLAY);

        bindOnClickPreferences();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return true;
    }

    private void bindOnClickPreferences() {
        defaultRowsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                int defaultRows = sharedPreferences.getInt(KEY_DEFAULT_ROWS, 3);
                View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_dimension_picker, null);
                final NumberPicker numberPicker = v.findViewById(R.id.numberPicker);
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(12);
                numberPicker.setValue(defaultRows);
                numberPicker.setWrapSelectorWheel(false);
                new AlertDialog.Builder(PreferenceActivity.this)
                        .setTitle(R.string.settings_default_rows_title)
                        .setView(v)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPreferences
                                        .edit()
                                        .putInt(KEY_DEFAULT_ROWS, numberPicker.getValue())
                                        .apply();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                return true;
            }
        });

        defaultColumnsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                int defaultRows = sharedPreferences.getInt(KEY_DEFAULT_COLUMNS, 3);
                View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_dimension_picker, null);
                final NumberPicker numberPicker = v.findViewById(R.id.numberPicker);
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(12);
                numberPicker.setValue(defaultRows);
                numberPicker.setWrapSelectorWheel(false);
                new AlertDialog.Builder(PreferenceActivity.this)
                        .setTitle(R.string.settings_default_rows_title)
                        .setView(v)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPreferences
                                        .edit()
                                        .putInt(KEY_DEFAULT_COLUMNS, numberPicker.getValue())
                                        .apply();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                return true;
            }
        });

        experimentalPreference.setChecked(sharedPreferences.getBoolean(KEY_EXPERIMENTAL, false));
        experimentalPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if ((boolean) o) {
                    new AlertDialog.Builder(preference.getContext())
                            .setTitle(R.string.settings_experimental_title)
                            .setMessage(R.string.confirm_experimental)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sharedPreferences.edit().putBoolean(KEY_EXPERIMENTAL, true).apply();
                                }
                            })
                            .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                } else {
                    sharedPreferences.edit().putBoolean(KEY_EXPERIMENTAL, false).apply();
                }
                return true;
            }
        });

        nightmodePreference.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
        nightmodePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if ((boolean) o) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

                Intent i = new Intent(getApplicationContext(), PreferenceActivity.class);
                finish();
                startActivity(i);
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
                App.writeEmail(PreferenceActivity.this);
                return false;
            }
        });

        checkManuallyUpdatePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startService(UpdateCheckerService.getUpdateCheckerService(getApplicationContext()));
                return true;
            }
        });

        openGooglePlayPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                App.openGooglePlay(getApplicationContext());
                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        App.initUpdaterBroadcastReceiver(getApplicationContext());
    }

    /**
     * STUFF METHOD TO ENABLE TOOLBAR
     * IT WORKS - SO DON'T TOUCH ANYTHING
     */
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Toolbar bar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
            root.addView(bar, 0); // insert at top
        } else {
            ViewGroup root = findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);

            root.removeAllViews();

            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);


            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            } else {
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);

            root.addView(content);
            root.addView(bar);
        }

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
