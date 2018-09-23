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
import android.view.View;
import android.widget.NumberPicker;

import kartollika.matrixcalc.R;


public class DimensionPickerDialog extends DialogFragment {

    public static final String KEY_CUR_DIMENSION = "cur_dimension";
    public static final String NEW_DIMENSION_VALUE = "new_dimension_value";

    private int curDimension;

    public static DimensionPickerDialog newInstance(int curDimension) {

        Bundle args = new Bundle();
        args.putInt(KEY_CUR_DIMENSION, curDimension);
        DimensionPickerDialog fragment = new DimensionPickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        curDimension = getArguments().getInt(KEY_CUR_DIMENSION);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_dimension_picker, null);
        final NumberPicker numberPicker = v.findViewById(R.id.numberPicker);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(12);
        numberPicker.setValue(curDimension);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                getArguments().putInt(KEY_CUR_DIMENSION, newVal);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.setup_dims)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(numberPicker.getValue());
                        dialog.dismiss();
                    }
                }).create();
    }

    private void sendResult(int dimensionChosen) {
        if (getTargetFragment() == null) return;

        Intent i = new Intent();
        i.putExtra(NEW_DIMENSION_VALUE, dimensionChosen);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
    }
}
