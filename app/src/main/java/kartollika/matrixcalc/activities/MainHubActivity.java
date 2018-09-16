package kartollika.matrixcalc.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import kartollika.matrixcalc.App;
import kartollika.matrixcalc.R;
import kartollika.matrixcalc.fragments.DefaultOperationsHubFragment;

public class MainHubActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return DefaultOperationsHubFragment.newInstance();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.main_activity;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mainhub_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_item_settings: {
                openSettings(this);
                break;
            }

            case R.id.menu_item_send_email: {
                App.writeEmail(this);
                break;
            }

            case R.id.menu_item_rate_in_gp: {
                break;
            }

            case R.id.menu_item_block_ads: {

            }
        }
        return true;
    }

    private void openSettings(Context context) {
        Intent i = new Intent(context, SettingsActivity.class);
        startActivity(i);
    }
}
