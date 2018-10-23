package kartollika.matrixcalc.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import es.dmoral.toasty.Toasty;
import kartollika.matrixcalc.ITextOutputFormat;
import kartollika.matrixcalc.MatrixManager;
import kartollika.matrixcalc.R;
import kartollika.matrixcalc.utilities.OperationHintsAndroid;
import kartollika.matrixcalc.utilities.TableMatrixLayout;
import kartollika.matrixmodules.matrix.AugmentedMatrix;
import kartollika.matrixmodules.matrix.DoubleMatrix;
import kartollika.matrixmodules.matrix.Matrix;
import kartollika.matrixmodules.operations.BinaryOperationStrategyManager;
import kartollika.matrixmodules.operations.Operation;
import kartollika.matrixmodules.operations.SteppingOperation;
import kartollika.matrixmodules.operations.UnaryOperationStrategyManager;
import kartollika.matrixmodules.operations.concrete.binary.OperationAddition;
import kartollika.matrixmodules.operations.concrete.binary.OperationPower;
import kartollika.matrixmodules.operations.concrete.binary.OperationSubtract;
import kartollika.matrixmodules.operations.concrete.binary.OperationTimes;
import kartollika.matrixmodules.operations.concrete.binary.OperationTimesByNumber;
import kartollika.matrixmodules.operations.concrete.unary.OperationDeterminant;
import kartollika.matrixmodules.operations.concrete.unary.OperationInverse;
import kartollika.matrixmodules.operations.concrete.unary.OperationSolveSystem;
import kartollika.matrixmodules.operations.concrete.unary.OperationTranspose;

import static kartollika.matrixcalc.activities.ShowResultActivity.KEY_COEFFICIENT;
import static kartollika.matrixcalc.activities.ShowResultActivity.KEY_OPERATION_TO_SOLVE;

public class ShowResultFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ShowResultFragment";

    private Button buttonActivateSteps;
    private Button buttonPreviousStep;
    private Button buttonNextStep;
    private TextView hints;
    private ProgressBar progressSteps;
    private Group groupStepByStep;
    private Operation operation;
    private Number coefficient;
    private Matrix result;
    private StepManager stepManager;
    private UnaryOperationStrategyManager unaryManager = new UnaryOperationStrategyManager();
    private BinaryOperationStrategyManager binaryManager = new BinaryOperationStrategyManager();
    private TableMatrixLayout table;
    private ITextOutputFormat formatter = TableMatrixLayout.DEFAULT_FORMATTER;
    private int formatterCnt = 0;

    public static ShowResultFragment newInstance(Operation operation) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_OPERATION_TO_SOLVE, operation);
        ShowResultFragment fragment = new ShowResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ShowResultFragment newInstance(Operation operation, Number coefficient) {
        ShowResultFragment instance = newInstance(operation);
        instance.getArguments().putSerializable(KEY_COEFFICIENT, coefficient);
        return instance;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        operation = (Operation) getArguments().getSerializable(KEY_OPERATION_TO_SOLVE);
        Serializable potentialCoefficient = getArguments().getSerializable(KEY_COEFFICIENT);
        if (potentialCoefficient != null) {
            coefficient = (Number) potentialCoefficient;
        }

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.show_result_activity_menu, menu);
        if (result == null) {
            menu.clear();
            return;
        }

        if (operation != Operation.SYSTEM_SOLVE) {
            menu.findItem(R.id.menu_item_into_system).setVisible(false);
        } else {
            menu.findItem(R.id.menu_item_into_A).setVisible(false);
            menu.findItem(R.id.menu_item_into_B).setVisible(false);
        }

        MenuItem formatterItem = menu.findItem(R.id.menu_item_convert_values);
        if (formatterCnt == 0) {
            formatterItem.setTitle(R.string.convert_to_doubles);
            formatterItem.setIcon(R.drawable.ic_convert_to_doubles);
        } else {
            formatterItem.setTitle(R.string.convert_to_rationales);
            formatterItem.setIcon(R.drawable.ic_convert_to_rationales);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MatrixManager matrixManager = MatrixManager.getInstance(requireContext());
        switch (item.getItemId()) {
            case R.id.menu_item_into_A: {
                matrixManager.setA(result);
                Toasty.success(requireContext(),
                        requireContext().getString(R.string.save_to_slot_1_success), Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.menu_item_into_B: {
                matrixManager.setB(result);
                Toasty.success(requireContext(),
                        requireContext().getString(R.string.save_to_slot_2_success), Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.menu_item_into_system: {
                matrixManager.setSystem((AugmentedMatrix) result);
                Toasty.success(requireContext(),
                        requireContext().getString(R.string.save_to_slot_success), Toast.LENGTH_SHORT).show();
            }

            case R.id.menu_item_convert_values: {
                if (formatterCnt == 0) {
                    table.setFormatter(TableMatrixLayout.DOUBLES_FORMATTER);
                } else {
                    table.setFormatter(TableMatrixLayout.RATIONALES_FORMATTER);
                }
                table.updateNumbers();

                formatterCnt = (formatterCnt + 1) % 2;
                requireActivity().invalidateOptionsMenu();
            }
        }
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_show_result, container, false);

        buttonActivateSteps = v.findViewById(R.id.buttonActivateSteps);
        buttonActivateSteps.setOnClickListener(this);

        buttonPreviousStep = v.findViewById(R.id.buttonPreviousStep);
        buttonPreviousStep.setOnClickListener(this);

        buttonNextStep = v.findViewById(R.id.buttonNextStep);
        buttonNextStep.setOnClickListener(this);

        groupStepByStep = v.findViewById(R.id.groupStepByStep);
        hints = v.findViewById(R.id.hints);
        hints.setMovementMethod(new ScrollingMovementMethod());
        table = v.findViewById(R.id.table);
        table.changeAccessType(TableMatrixLayout.READ_ONLY);
        progressSteps = v.findViewById(R.id.stepsProgressBar);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonActivateSteps: {
                activateSteps();
                break;
            }

            case R.id.buttonPreviousStep: {
                previousStep();
                break;
            }

            case R.id.buttonNextStep: {
                nextStep();
            }

        }
    }

    private void activateSteps() {
        buttonActivateSteps.setVisibility(View.GONE);
        groupStepByStep.setVisibility(View.VISIBLE);
        hints.setVisibility(View.VISIBLE);

        stepManager = new StepManager(unaryManager.getSteps());
        updateCurrentStepInformation(stepManager.getStepByCursor());
    }

    @Override
    public void onResume() {
        super.onResume();

        MatrixManager matrixManager = MatrixManager.getInstance(requireContext());
        SteppingOperation steppingOperation = new SteppingOperation();
        steppingOperation.setTextHints(new OperationHintsAndroid(requireContext()));

        switch (operation) {
            case ADDITION: {
                binaryManager.setStrategy(new OperationAddition());
                new SolveOperationTask(binaryManager).execute(matrixManager.getA(), matrixManager.getB());
                break;
            }
            case ADDITITION_REVERSE: {
                binaryManager.setStrategy(new OperationAddition());
                new SolveOperationTask(binaryManager).execute(matrixManager.getB(), matrixManager.getA());
                break;
            }

            case SUBTRACT: {
                binaryManager.setStrategy(new OperationSubtract());
                new SolveOperationTask(binaryManager).execute(matrixManager.getA(), matrixManager.getB());
                break;
            }
            case SUBTRACT_REVERSE: {
                binaryManager.setStrategy(new OperationSubtract());
                new SolveOperationTask(binaryManager).execute(matrixManager.getB(), matrixManager.getA());
                break;
            }

            case MULTIPLY_BY_NUMBER: {
                binaryManager.setStrategy(new OperationTimesByNumber());
                new SolveOperationTask(binaryManager).execute(matrixManager.getA(), coefficient);
                break;
            }
            case MULTIPLY_BY_NUMBER_REVERSE: {
                binaryManager.setStrategy(new OperationTimesByNumber());
                new SolveOperationTask(binaryManager).execute(matrixManager.getB(), coefficient);
                break;
            }

            case MULTIPLY: {
                binaryManager.setStrategy(new OperationTimes());
                new SolveOperationTask(binaryManager).execute(matrixManager.getA(), matrixManager.getB());
                break;
            }
            case MULTIPLY_REVERSE: {
                binaryManager.setStrategy(new OperationTimes());
                new SolveOperationTask(binaryManager).execute(matrixManager.getB(), matrixManager.getA());
                break;
            }

            case DETERMINANT: {
                unaryManager.setStrategy(new OperationDeterminant(steppingOperation));
                new SolveOperationTask(unaryManager).execute(matrixManager.getA());
                break;
            }
            case DETERMINANT_REVERSE: {
                unaryManager.setStrategy(new OperationDeterminant(steppingOperation));
                new SolveOperationTask(unaryManager).execute(matrixManager.getB());
                break;
            }

            case INVERSE: {
                unaryManager.setStrategy(new OperationInverse(steppingOperation));
                new SolveOperationTask(unaryManager).execute(matrixManager.getA());
                break;
            }
            case INVERSE_REVERSE: {
                unaryManager.setStrategy(new OperationInverse(steppingOperation));
                new SolveOperationTask(unaryManager).execute(matrixManager.getB());
                break;
            }

            case TRANSPOSE: {
                unaryManager.setStrategy(new OperationTranspose());
                new SolveOperationTask(unaryManager).execute(matrixManager.getA());
                break;
            }
            case TRANSPOSE_REVERSE: {
                unaryManager.setStrategy(new OperationTranspose());
                new SolveOperationTask(unaryManager).execute(matrixManager.getB());
                break;
            }

            case POWER: {
                binaryManager.setStrategy(new OperationPower(steppingOperation));
                new SolveOperationTask(binaryManager).execute(matrixManager.getA(), coefficient);
                break;
            }
            case POWER_REVERSE: {
                binaryManager.setStrategy(new OperationPower(steppingOperation));
                new SolveOperationTask(binaryManager).execute(matrixManager.getB(), coefficient);
                break;
            }

            case SYSTEM_SOLVE: {
                unaryManager.setStrategy(new OperationSolveSystem(steppingOperation));
                new SolveOperationTask(unaryManager).execute(matrixManager.getMatrixSystem());
                break;
            }

            default: {
                Toasty.error(requireContext(), "Operation wasn't chosen", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void nextStep() {
        Object[] thisStep = stepManager.nextStep();
        updateCurrentStepInformation(thisStep);
    }

    private void previousStep() {
        Object[] thisStep = stepManager.previousStep();
        updateCurrentStepInformation(thisStep);
    }

    private void updateCurrentStepInformation(Object[] thisStep) {
        if (thisStep == null) return;

        if (thisStep[0] instanceof DoubleMatrix) {
            table.setTable((DoubleMatrix) thisStep[0]);
        } else if (thisStep[0] instanceof AugmentedMatrix) {
            table.setTable((AugmentedMatrix) thisStep[0]);
        } else {
            table.setTable((Matrix) thisStep[0]);
        }
        table.updateNumbers();
        hints.setText(Html.fromHtml((String) thisStep[1]));
    }

    private class SolveOperationTask extends AsyncTask<Object, Void, Matrix> {

        private BinaryOperationStrategyManager binaryOperationStrategyManager;
        private UnaryOperationStrategyManager unaryOperationStrategyManager;


        SolveOperationTask(BinaryOperationStrategyManager binaryOperationStrategyManager) {
            this.binaryOperationStrategyManager = binaryOperationStrategyManager;
        }

        SolveOperationTask(UnaryOperationStrategyManager unaryOperationStrategyManager) {
            this.unaryOperationStrategyManager = unaryOperationStrategyManager;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Matrix doInBackground(Object... objects) {
            Matrix result;
            if (unaryOperationStrategyManager == null) {
                if (objects[1] instanceof Number && objects[0] instanceof Matrix) {
                    result = binaryOperationStrategyManager.executeStrategy((Matrix) objects[0], (Number) objects[1]);
                } else {
                    if (objects[1] instanceof Matrix && objects[0] instanceof Matrix) {
                        result = binaryOperationStrategyManager.executeStrategy((Matrix) objects[0], (Matrix) objects[1]);
                    } else {
                        throw new IllegalArgumentException("Passed wrong data");
                    }
                }
            } else {
                if (objects[0] instanceof AugmentedMatrix) {
                    result = unaryOperationStrategyManager.executeStrategy((AugmentedMatrix) objects[0]);
                } else if (objects[0] instanceof Matrix) {
                    result = unaryOperationStrategyManager.executeStrategy((Matrix) objects[0]);
                } else {
                    throw new IllegalArgumentException("Passed wrong data");
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Matrix matrix) {
            super.onPostExecute(matrix);
            result = matrix;

            if (unaryManager.getSteps() != null) {
                buttonActivateSteps.setVisibility(View.VISIBLE);
                if (unaryManager.getSteps().size() == 0) {
                    buttonActivateSteps.setEnabled(false);
                }
            }

            if (matrix == null) {
                switch (operation) {
                    case INVERSE:
                    case INVERSE_REVERSE: {
                        TextView noMatrix = new TextView(requireContext());
                        noMatrix.setText(R.string.matrix_does_not_exist);
                        noMatrix.setTextSize(16);
                        table.addView(noMatrix);
                        buttonActivateSteps.setEnabled(false);
                        return;
                    }
                }
            }

            if (operation == Operation.SYSTEM_SOLVE && result.equals(new AugmentedMatrix(result.getRows(), result.getColumns()))) {
                hints.setVisibility(View.VISIBLE);
                hints.setText(R.string.nothing_to_solve);
                buttonActivateSteps.setEnabled(false);
                table.initTable(result);
                return;
            }

            if (operation == Operation.SYSTEM_SOLVE) {
                hints.setVisibility(View.VISIBLE);
                hints.setText(Html.fromHtml((String) unaryManager.getSteps().get(unaryManager.getSteps().size() - 1)[1]));
            }

            requireActivity().invalidateOptionsMenu();

            table.initTable(matrix);
        }
    }

    private class StepManager {

        private List<Object[]> steps;
        private int cursor = 0;

        StepManager(List<Object[]> steps) {
            this.steps = steps;
            progressSteps.setProgress(0);
            progressSteps.setMax(steps.size() - 1);
        }

        Object[] nextStep() {
            if (cursor == steps.size() - 1) return null;
            progressSteps.incrementProgressBy(1);
            return steps.get(++cursor);
        }

        Object[] previousStep() {
            if (cursor == 0) return null;
            progressSteps.incrementProgressBy(-1);
            return steps.get(--cursor);
        }

        Object[] getStepByCursor() {
            return steps.get(cursor);
        }
    }
}
