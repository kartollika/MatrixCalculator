package kartollika.matrixcalc.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import kartollika.matrixcalc.R;
import kartollika.matrixcalc.SolveCallback;
import kartollika.matrixcalc.activities.InputMatrixActivity;
import kartollika.matrixcalc.activities.ShowResultActivity;
import kartollika.matrixmodules.operations.Operation;

public class LinearSystemHubFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "LinearSystemHubFragment";
    private SolveCallback solveCallback;
    private Button buttonSystem;
    private Button buttonSolve;

    public static LinearSystemHubFragment newInstance() {

        Bundle args = new Bundle();

        LinearSystemHubFragment fragment = new LinearSystemHubFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_linear_system, container, false);
        buttonSystem = v.findViewById(R.id.buttonSystem);
        buttonSystem.setOnClickListener(this);

        buttonSolve = v.findViewById(R.id.buttonSolve);
        buttonSolve.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSystem: {
                Intent i = new Intent(requireContext(), InputMatrixActivity.class);
                i.putExtra(InputMatrixActivity.KEY_MATRIX_TYPE, 2);
                startActivity(i);
                break;
            }

            case R.id.buttonSolve: {
                Intent i = new Intent(getActivity(), ShowResultActivity.class);
                i.putExtra(ShowResultActivity.KEY_OPERATION_TO_SOLVE, Operation.SYSTEM_SOLVE);
                startActivity(i);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        solveCallback = (SolveCallback) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        solveCallback = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
