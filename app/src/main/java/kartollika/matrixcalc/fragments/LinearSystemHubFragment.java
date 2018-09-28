package kartollika.matrixcalc.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import kartollika.matrixcalc.MatrixManager;
import kartollika.matrixcalc.R;
import kartollika.matrixcalc.SolveCallback;
import kartollika.matrixcalc.activities.InputMatrixActivity;
import kartollika.matrixcalc.activities.ShowResultActivity;
import kartollika.matrixmodules.operations.Operation;

import static kartollika.matrixcalc.fragments.DefaultOperationsHubFragment.REQUEST_SOLVE;

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
                startActivityForResult(i, REQUEST_SOLVE);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        solveCallback = (SolveCallback) getActivity();
    }

    @Override
    public void onStart() {
        updateDimensionsOnButtons();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDimensionsOnButtons();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        solveCallback = null;
    }

    private void updateDimensionsOnButtons() {
        MatrixManager matrixManager = MatrixManager.getInstance(requireContext());
        String sSystem = "<big>B</big><sup><small>" + String.valueOf(matrixManager.getMatrixSystem().getRows())
                + "x" + String.valueOf(matrixManager.getMatrixSystem().getColumns()) + "</small></sup>";
        buttonSystem.setText(Html.fromHtml(sSystem));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SOLVE) {
            solveCallback.onSolve();
            return;
        }

        if (resultCode != Activity.RESULT_OK) {
        }
    }
}
