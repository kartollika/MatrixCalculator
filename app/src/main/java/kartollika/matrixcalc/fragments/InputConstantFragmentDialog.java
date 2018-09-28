package kartollika.matrixcalc.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import kartollika.matrixcalc.R;
import kartollika.matrixmodules.RationalNumber;
import kartollika.matrixmodules.operations.Operation;

public class InputConstantFragmentDialog extends DialogFragment {

    public static String KEY_TYPE_OF_NUMBER = "type_of_number";
    public static String KEY_INPUT_CONSTANT = "input_constant";

    private Operation operation;

    public static InputConstantFragmentDialog newInstance(Operation operation) {

        Bundle args = new Bundle();
        args.putSerializable(KEY_TYPE_OF_NUMBER, operation);
        InputConstantFragmentDialog fragment = new InputConstantFragmentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        operation = (Operation) getArguments().getSerializable(KEY_TYPE_OF_NUMBER);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View v = inflater.inflate(R.layout.dialog_constant_requester, null);
        final EditText editTextConstantInputField = v.findViewById(R.id.inputConstantField);
        if (operation.equals(Operation.MULTIPLY_BY_NUMBER) || operation.equals(Operation.MULTIPLY_BY_NUMBER_REVERSE)) {
            editTextConstantInputField.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (operation.equals(Operation.POWER) || operation.equals(Operation.POWER_REVERSE)) {
            editTextConstantInputField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

        return new AlertDialog.Builder(requireContext())
                .setTitle("Input constant")
                .setView(v)
                .setMessage("Supported symbols:\n" + getSupportSymbols(operation))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(editTextConstantInputField.getText());
                    }
                })
                .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
    }

    private String getSupportSymbols(Operation operation) {
        switch (operation) {
            case MULTIPLY_BY_NUMBER:
            case MULTIPLY_BY_NUMBER_REVERSE:
                return "0 1 2 3 4 5 6 7 8 9 / - .";

            case POWER:
            case POWER_REVERSE:
                return "0 1 2 3 4 5 6 7 8 9";

            default:
                return "";
        }
    }

    private <T extends CharSequence> void sendResult(T inputText) {
        if (getTargetFragment() == null) return;

        Number inputNumber;
        try {
            switch (operation) {
                case POWER:
                case POWER_REVERSE: {
                    inputNumber = Integer.parseInt(String.valueOf(inputText));
                    break;
                }

                case MULTIPLY_BY_NUMBER:
                case MULTIPLY_BY_NUMBER_REVERSE: {
                    inputNumber = RationalNumber.parseRational(String.valueOf(inputText));
                    break;
                }

                default: {
                    inputNumber = RationalNumber.parseRational(String.valueOf(inputText));
                }
            }
        } catch (NumberFormatException nfe) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
            return;
        }

        Intent i = new Intent();
        i.putExtra(KEY_INPUT_CONSTANT, inputNumber);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
    }
}
