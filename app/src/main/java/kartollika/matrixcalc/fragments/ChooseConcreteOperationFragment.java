package kartollika.matrixcalc.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import kartollika.matrixcalc.R;
import kartollika.matrixmodules.operations.Operation;

public abstract class ChooseConcreteOperationFragment extends Fragment {

    protected abstract int calcId(int i, int j);

    protected abstract String[] getAvailableOperationsForFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_operation_fragment, container, false);

        Operation operation = (Operation) getActivity().getIntent().getSerializableExtra("operation_chosen");
        final String[] strings = getAvailableOperationsForFragment();

        for (int i = 0; i < 2; ++i) {
            LinearLayout linearLayout;
            if (i == 0) {
                linearLayout = view.findViewById(R.id.ll0);
            } else {
                linearLayout = view.findViewById(R.id.ll1);
            }
            for (int j = 0; j < 4; ++j) {
                Button button = (Button) inflater.inflate(R.layout.item_choose_oper, container, false);
                final int id = calcId(i, j);
                int intOperation = operation.compareTo(Operation.ADDITION);

                button.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("operation_chosen", Operation.values()[v.getId() + 1]);
                        String s = strings[v.getId() - offset()];
                        intent.putExtra("operation_text", s);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                    }
                });
                if (id == intOperation) {
                    button.setBackgroundColor(getResources().getColor(R.color.colorOperationChoosed));
                }
                button.setId(id);

                button.setText(Html.fromHtml(strings[id - offset()]));
                linearLayout.addView(button);
            }
        }
        return view;
    }

    protected abstract int offset();
}
