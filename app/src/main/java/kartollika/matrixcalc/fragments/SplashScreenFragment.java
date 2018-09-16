package kartollika.matrixcalc.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kartollika.matrixcalc.activities.MainHubActivity;
import kartollika.matrixcalc.R;

public class SplashScreenFragment extends Fragment {

    private static final int SPLASH_SCREEN_TIMEOUT = 1500;

    public static SplashScreenFragment newInstance() {
        return new SplashScreenFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_activity, container, false);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMainActivity();
            }
        }, SPLASH_SCREEN_TIMEOUT);
    }

    private void loadMainActivity() {
        Intent i = new Intent(getContext(), MainHubActivity.class);
        startActivity(i);
    }
}
