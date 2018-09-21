package kartollika.matrixcalc.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdView;

import kartollika.matrixcalc.R;

public class ShowResultFragment extends Fragment {

    private Button buttonActivateSteps;
    private Button buttonPreviousStep;
    private Button buttonNexrStep;
    private ProgressBar progressBarSteps;

    public static ShowResultFragment newInstance() {

        Bundle args = new Bundle();

        ShowResultFragment fragment = new ShowResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_show_result, container, false);
        return v;
    }
}
