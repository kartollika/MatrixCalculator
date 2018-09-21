package kartollika.matrixcalc.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kartollika.matrixcalc.R;

public class Unaries extends ChooseConcreteOperationFragment {

    public Unaries() {
        super();
    }


    @Override
    protected int calcId(int i, int j) {
        return 8 + i * 4 + j;
    }

    @Override
    protected String[] getAvailableOperationsForFragment() {
        return getActivity().getResources().getStringArray(R.array.unaryOperations);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int offset() {
        return 8;
    }
}
