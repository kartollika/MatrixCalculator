package kartollika.matrixcalc.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;

import kartollika.matrixcalc.R;
import kartollika.matrixcalc.fragments.Binaries;
import kartollika.matrixcalc.fragments.Unaries;
import kartollika.matrixmodules.operations.Operation;

public class ChooseOperationActivity extends AppCompatActivity {

    public static final String TAG = "ChooseOperationActivity";

    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.ChooseOperationActivityDialogDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_operation);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        Operation oper = (Operation) getIntent().getSerializableExtra("operation_chosen");

        if (oper.compareTo(Operation.INVERSE) < 0) {
            fragmentTransaction.replace(R.id.container, new Binaries()).commit();
            switchTab(0);
        } else if (oper.compareTo(Operation.INVERSE) >= 0) {
            fragmentTransaction.replace(R.id.container, new Unaries()).commit();
            switchTab(1);
        }
    }

    private void highlightTab(int id_Highlight, int id_Old) {
        ((Button) findViewById(id_Highlight)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        findViewById(id_Highlight).setBackgroundColor(getResources().getColor(R.color.colorButtonOperation));

        ((Button) findViewById(id_Old)).setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        findViewById(id_Old).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    private void switchTab(int i) {
        int tab1 = R.id.binariesButton;
        int tab2 = R.id.unariesButton;
        if (i == 0) {
            highlightTab(tab1, tab2);
        } else {
            highlightTab(tab2, tab1);
        }
    }

    public void onClick(View view) {
        int ID = view.getId();

        switch (ID) {
            case R.id.binariesButton:
                fragmentManager = getSupportFragmentManager();
                try {
                    Binaries binaries = (Binaries) fragmentManager.findFragmentById(R.id.container);
                } catch (Exception e) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.enter_left, R.anim.exit_right);
                    fragmentTransaction.replace(R.id.container, new Binaries()).commit();
                    switchTab(0);
                }
                break;

            case R.id.unariesButton:
                fragmentManager = getSupportFragmentManager();
                try {
                    Unaries unaries = (Unaries) fragmentManager.findFragmentById(R.id.container);
                } catch (Exception e) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.enter_right, R.anim.exit_left);
                    fragmentTransaction.replace(R.id.container, new Unaries()).commit();
                    switchTab(1);
                }

                break;
        }
    }
}
