package kartollika.matrixcalc.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.NumberPicker;

import kartollika.matrixcalc.R;

public class DimensionPickerDialog extends DialogFragment {

    private static final String KEY_CUR_DIMENSION = "cur_dimension";
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
        NumberPicker numberPicker = v.findViewById(R.id.numberPicker);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(12);
        numberPicker.setValue(curDimension);
        numberPicker.setWrapSelectorWheel(false);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Test")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }
}
