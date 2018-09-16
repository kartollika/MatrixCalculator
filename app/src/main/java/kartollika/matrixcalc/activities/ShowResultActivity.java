package kartollika.matrixcalc.activities;

import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

import kartollika.matrixcalc.R;
import kartollika.matrixcalc.fragments.ShowResultFragment;

public class ShowResultActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return ShowResultFragment.newInstance();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_activity;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        return super.onCreateOptionsMenu(menu);
    }
}
