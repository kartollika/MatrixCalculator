package kartollika.matrixcalc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class DefaultOperationsHubFragment extends Fragment {

    public static final String TAG = "DefaultOperationsHubFragment";

    public static DefaultOperationsHubFragment newInstance() {

        Bundle args = new Bundle();

        DefaultOperationsHubFragment fragment = new DefaultOperationsHubFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
