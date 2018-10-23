package kartollika.matrixcalc.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import kartollika.matrixcalc.R;


public class DimensionPickerDialog extends DialogFragment {

    public static final String KEY_CUR_DIMENSION = "cur_dimension";
    public static final String NEW_DIMENSION_VALUE = "new_dimension_value";
    public static final String KEY_WHAT_DIMENSION_TO_SET = "what_dimension";

    private int curDimension;
    private int whatDimension;

    public static DimensionPickerDialog newInstance(int curDimension, int what) {

        Bundle args = new Bundle();
        args.putInt(KEY_CUR_DIMENSION, curDimension);
        args.putInt(KEY_WHAT_DIMENSION_TO_SET, what);
        DimensionPickerDialog fragment = new DimensionPickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        curDimension = getArguments().getInt(KEY_CUR_DIMENSION);
        whatDimension = getArguments().getInt(KEY_WHAT_DIMENSION_TO_SET);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = requireActivity().getLayoutInflater().inflate(R.layout.dialog_dimension_picker, null);
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
                .setTitle(whatDimension == 0 ? R.string.settings_default_rows_title : R.string.settings_default_columns_title)
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
