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

import kartollika.matrixcalc.R;
import kartollika.matrixcalc.SolveCallback;
import kartollika.matrixcalc.activities.ChooseOperationActivity;
import kartollika.matrixcalc.activities.InputMatrixActivity;
import kartollika.matrixcalc.activities.ShowResultActivity;

public class DefaultOperationsHubFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "DefaultOperationsHubFragment";
    private static final int REQUEST_OPERATION = 0;
    private static final int MATRIX_INPUT = 1;
    private static final int SOLVE = 2;
    int[] arr1;
    int[] arr2;
    private Button buttonA;
    private Button buttonB;
    private Button buttonChooseOperation;
    private Button buttonSolve;
    private SolveCallback solveCallback;

    private int curOperation = -1;

    public static DefaultOperationsHubFragment newInstance() {

        Bundle args = new Bundle();

        DefaultOperationsHubFragment fragment = new DefaultOperationsHubFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arr1 = getResources().getIntArray(R.array.binaryOperations);
        arr2 = getResources().getIntArray(R.array.unaryOperations);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_operations, container, false);

        buttonA = v.findViewById(R.id.buttonA);
        buttonA.setOnClickListener(this);

        buttonB = v.findViewById(R.id.buttonB);
        buttonB.setOnClickListener(this);

        buttonChooseOperation = v.findViewById(R.id.buttonChooseOperations);
        buttonChooseOperation.setOnClickListener(this);

        buttonSolve = v.findViewById(R.id.buttonSolve);
        buttonSolve.setOnClickListener(this);

        return v;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonA: {
                Intent i = new Intent(getActivity(), InputMatrixActivity.class);
                startActivity(i);
                break;
            }

            case R.id.buttonB: {
                Intent i = new Intent(getActivity(), InputMatrixActivity.class);
                startActivity(i);
                break;
            }

            case R.id.buttonChooseOperations: {
                Intent intent = new Intent(getContext(), ChooseOperationActivity.class);
                intent.putExtra("operation_chosen", curOperation);
                startActivityForResult(intent, REQUEST_OPERATION);
                break;
            }

            case R.id.buttonSolve: {
                Intent i = new Intent(getActivity(), ShowResultActivity.class);
                startActivity(i);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            if (requestCode != SOLVE) {
                return;
            }

            if (resultCode != Activity.RESULT_OK) {
                return;
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_OPERATION: {
                    if (requestCode == REQUEST_OPERATION) {
                        curOperation = data.getIntExtra("operation_chosen", -1);
                        String s = data.getStringExtra("operation_text");
                        updateOperationButton(s);
                    }
                    break;
                }

                case MATRIX_INPUT: {

                    break;
                }
                /*case MATRIX_SYSTEM:
                 *//*if (data.getExtras() != null) {
                        Matrix pmatrix = data.getExtras().getParcelable("matrix");
                        if (pmatrix != null) {
                            matrix = pmatrix;
                            matrix.setEdited();
                        } else {
                            return;
                        }
                        App.systemMatrix = matrix;
                    }*//*
                    break;*/

                case SOLVE: {
                    solveCallback.onSolve();
                    break;
                }
            }
        }
    }

    private <T extends CharSequence> void updateOperationButton(T curOperation) {
        buttonChooseOperation.setText(Html.fromHtml(String.valueOf(curOperation)));
    }
}
