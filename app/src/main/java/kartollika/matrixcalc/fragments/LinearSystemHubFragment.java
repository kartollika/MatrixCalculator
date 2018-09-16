package kartollika.matrixcalc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class LinearSystemHubFragment extends Fragment {

    public static final String TAG = "LinearSystemHubFragment";

    public static LinearSystemHubFragment newInstance() {
        
        Bundle args = new Bundle();
        
        LinearSystemHubFragment fragment = new LinearSystemHubFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
