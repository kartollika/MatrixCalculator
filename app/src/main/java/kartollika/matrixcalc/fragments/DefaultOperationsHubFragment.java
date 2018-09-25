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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;
import kartollika.matrixcalc.IOperationSave;
import kartollika.matrixcalc.MatrixManager;
import kartollika.matrixcalc.R;
import kartollika.matrixcalc.SolveCallback;
import kartollika.matrixcalc.activities.ChooseOperationActivity;
import kartollika.matrixcalc.activities.InputMatrixActivity;
import kartollika.matrixcalc.activities.ShowResultActivity;
import kartollika.matrixmodules.matrix.Matrix;
import kartollika.matrixmodules.operations.Operation;

import static kartollika.matrixcalc.activities.InputMatrixActivity.KEY_MATRIX_TYPE;

public class DefaultOperationsHubFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "DefaultOperationsHubFragment";
    public static final String KEY_OPERATION_CHOSEN = "operation_chosen";
    public static final String KEY_OPERATION_TEXT = "operation_text";

    private static final int REQUEST_OPERATION = 0;
    private static final int REQUEST_SOLVE = 1;
    private static final int MATRIX_INPUT = 1;
    private static final int SOLVE = 2;
    private Button buttonA;
    private Button buttonB;
    private Button buttonChooseOperation;
    private Button buttonSolve;
    private SolveCallback solveCallback;
    private IOperationSave iOperationSave;
    private Operation curOperation = Operation.NONE;

    public static DefaultOperationsHubFragment newInstance(Operation operation) {

        Bundle args = new Bundle();
        args.putSerializable(KEY_OPERATION_CHOSEN, operation);
        DefaultOperationsHubFragment fragment = new DefaultOperationsHubFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        curOperation = (Operation) getArguments().getSerializable(KEY_OPERATION_CHOSEN);
    }

    private void updateChosenOperation(Operation curOperation) {
        String[] binariesStrings = requireContext().getResources().getStringArray(R.array.binaryOperations);
        String[] unariesStrings = requireContext().getResources().getStringArray(R.array.unaryOperations);

        List<String> list = new ArrayList<>();
        Collections.addAll(list, binariesStrings);
        Collections.addAll(list, unariesStrings);

        int offset = curOperation.compareTo(Operation.ADDITION);
        if (offset < 0) {
            return;
        }

        buttonChooseOperation.setText(Html.fromHtml(list.get(offset)));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateChosenOperation(curOperation);
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
        iOperationSave = (IOperationSave) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        solveCallback = null;
        iOperationSave = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonA: {
                Intent i = new Intent(getActivity(), InputMatrixActivity.class);
                i.putExtra(KEY_MATRIX_TYPE, 0);
                startActivity(i);
                break;
            }

            case R.id.buttonB: {
                Intent i = new Intent(getActivity(), InputMatrixActivity.class);
                i.putExtra(KEY_MATRIX_TYPE, 1);
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
                if (curOperation == Operation.NONE) {
                    Toasty.warning(requireContext(), getString(R.string.choose_one), Toast.LENGTH_SHORT).show();
                    onClick(buttonChooseOperation);
                    return;
                }

                if (checkMatrixDimensionsCompabilities(curOperation)) {
                    Intent i = new Intent(getActivity(), ShowResultActivity.class);
                    i.putExtra(ShowResultActivity.KEY_OPERATION_TO_SOLVE, curOperation);
                    startActivityForResult(i, REQUEST_SOLVE);
                } else {
                    Toasty.error(requireContext(),
                            getString(R.string.invalid_dimensions), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean checkMatrixDimensionsCompabilities(Operation curOperation) {
        MatrixManager matrixManager = MatrixManager.getInstance(requireContext());
        Matrix a = matrixManager.getA();
        Matrix b = matrixManager.getB();
        switch (curOperation) {
            case ADDITION:
            case ADDITITION_REVERSE:
            case SUBTRACT:
            case SUBTRACT_REVERSE: {
                if (a.getRows() != b.getRows() || a.getColumns() != b.getColumns()) {
                    return false;
                }
                break;
            }

            case MULTIPLY: {
                if (a.getColumns() != b.getRows()) {
                    return false;
                }
                break;
            }

            case MULTIPLY_REVERSE: {
                if (b.getColumns() != a.getRows()) {
                    return false;
                }
                break;
            }

            case POWER:
            case DETERMINANT: {
                if (a.getRows() != a.getColumns()) {
                    return false;
                }
                break;
            }

            case POWER_REVERSE:
            case DETERMINANT_REVERSE: {
                if (b.getRows() != b.getColumns()) {
                    return false;
                }
                break;
            }

            default: {
                return true;
            }
        }
        return true;
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
                    curOperation = (Operation) data.getSerializableExtra(KEY_OPERATION_CHOSEN);
                    String s = data.getStringExtra(KEY_OPERATION_TEXT);
                    updateOperationButton(s);

                    break;
                }

                case SOLVE: {
                    solveCallback.onSolve();
                    break;
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        iOperationSave.saveOperation(curOperation);
    }

    private <T extends CharSequence> void updateOperationButton(T curOperation) {
        buttonChooseOperation.setText(Html.fromHtml(String.valueOf(curOperation)));
    }
}
