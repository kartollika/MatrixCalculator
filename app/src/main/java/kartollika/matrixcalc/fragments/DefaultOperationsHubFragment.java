package kartollika.matrixcalc.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import static kartollika.matrixcalc.activities.ShowResultActivity.KEY_COEFFICIENT;
import static kartollika.matrixcalc.fragments.InputConstantFragmentDialog.KEY_INPUT_CONSTANT;

public class DefaultOperationsHubFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "DefaultOperHubFragment";
    public static final String KEY_OPERATION_CHOSEN = "operation_chosen";
    public static final String KEY_OPERATION_TEXT = "operation_text";
    public static final int REQUEST_SOLVE = 1;
    private static final String DIALOG_CONSTANT = "input_constant";
    private static final int REQUEST_OPERATION = 0;
    private static final int REQUEST_CONSTANT = 2;
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

                if (curOperation == Operation.MULTIPLY_BY_NUMBER
                        || curOperation == Operation.MULTIPLY_BY_NUMBER_REVERSE
                        || curOperation == Operation.POWER
                        || curOperation == Operation.POWER_REVERSE) {
                    InputConstantFragmentDialog inputConstantFragmentDialog = InputConstantFragmentDialog.newInstance(curOperation);
                    inputConstantFragmentDialog.setTargetFragment(DefaultOperationsHubFragment.this, REQUEST_CONSTANT);
                    inputConstantFragmentDialog.show(requireFragmentManager(), DIALOG_CONSTANT);
                    return;
                }

                startSolving(curOperation);
            }
        }
    }

    private Intent getSolverIntent(Operation operation) {
        Intent i = new Intent(getActivity(), ShowResultActivity.class);
        i.putExtra(ShowResultActivity.KEY_OPERATION_TO_SOLVE, curOperation);
        return i;
    }

    private void startSolving(Operation operation) {
        if (!checkMatrixDimensionsCompabilities(curOperation)) {
            Toasty.error(requireContext(),
                    getString(R.string.invalid_dimensions), Toast.LENGTH_LONG).show();
            return;
        }
        Intent i = getSolverIntent(curOperation);
        startActivityForResult(i, REQUEST_SOLVE);
    }

    private void startSolving(Operation operation, Number coefficient) {
        if (!checkMatrixDimensionsCompabilities(curOperation)) {
            Toasty.error(requireContext(),
                    getString(R.string.invalid_dimensions), Toast.LENGTH_LONG).show();
            return;
        }
        Intent i = getSolverIntent(operation);
        i.putExtra(KEY_COEFFICIENT, coefficient);
        startActivityForResult(i, REQUEST_SOLVE);
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
    public void onStart() {
        updateDimensionsOnButtons();
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == REQUEST_CONSTANT) {
                Toasty.error(requireContext(), getString(R.string.number_not_found), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (requestCode == REQUEST_SOLVE) {
            solveCallback.onSolve();
            return;
        }

        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_OPERATION: {
                curOperation = (Operation) data.getSerializableExtra(KEY_OPERATION_CHOSEN);
                String s = data.getStringExtra(KEY_OPERATION_TEXT);
                updateOperationButton(s);

                break;
            }

            case REQUEST_CONSTANT: {
                Number coefficient = (Number) data.getSerializableExtra(KEY_INPUT_CONSTANT);
                startSolving(curOperation, coefficient);
            }
        }
    }

    private void updateDimensionsOnButtons() {
        MatrixManager matrixManager = MatrixManager.getInstance(requireContext());
        String sA = "<big>A</big><sup><small>" + String.valueOf(matrixManager.getA().getRows())
                + "x" + String.valueOf(matrixManager.getA().getColumns()) + "</small></sup>";
        buttonA.setText(Html.fromHtml(sA));

        String sB = "<big>B</big><sup><small>" + String.valueOf(matrixManager.getB().getRows())
                + "x" + String.valueOf(matrixManager.getB().getColumns()) + "</small></sup>";
        buttonB.setText(Html.fromHtml(sB));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDimensionsOnButtons();
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
