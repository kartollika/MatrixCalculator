package kartollika.matrixcalc.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import kartollika.matrixcalc.R;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    protected abstract int getLayoutView();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.MainHubActivityThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(getLayoutView());

        FragmentManager fm = getSupportFragmentManager();
        Fragment currFragment = fm.findFragmentById(R.id.fragment_container);
        if (currFragment == null) {
            currFragment = createFragment();
            fm.beginTransaction()
                    .replace(R.id.fragment_container, currFragment)
                    .commit();
        }
    }
}
