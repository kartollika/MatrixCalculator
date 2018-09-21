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

import kartollika.matrixcalc.R;
import kartollika.matrixcalc.activities.MainHubActivity;

public class SplashScreenFragment extends Fragment {

    private static final int SPLASH_SCREEN_TIMEOUT = 1500;

    public static SplashScreenFragment newInstance() {

        Bundle args = new Bundle();

        SplashScreenFragment fragment = new SplashScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMainActivity();
            }
        }, SPLASH_SCREEN_TIMEOUT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.splash_screen_fragment, container, false);
        return v;
    }

    private void loadMainActivity() {
        Intent i = new Intent(getActivity().getApplicationContext(), MainHubActivity.class);
        startActivity(i);
        getActivity().finish();
    }
}
