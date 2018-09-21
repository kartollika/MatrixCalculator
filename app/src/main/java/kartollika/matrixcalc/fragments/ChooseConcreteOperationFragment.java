package kartollika.matrixcalc.fragments;

import android.app.Activity;
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
import android.widget.LinearLayout;

import kartollika.matrixcalc.R;

public abstract class ChooseConcreteOperationFragment extends Fragment {

    protected abstract int calcId(int i, int j);

    protected abstract String[] getAvailableOperationsForFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_operation_fragment, container, false);

        int operation = getActivity().getIntent().getIntExtra("operation_chosen", -1);
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
                int id = calcId(i, j);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button btn = (Button) v;
                        Intent intent = new Intent();
                        intent.putExtra("operation_chosen", v.getId());
                        String s = strings[v.getId() - offset()];
                        intent.putExtra("operation_text", s);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                    }
                });
                if (id == operation) {
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
