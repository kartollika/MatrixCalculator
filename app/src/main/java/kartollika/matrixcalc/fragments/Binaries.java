package kartollika.matrixcalc.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kartollika.matrixcalc.R;
import kartollika.matrixcalc.fragments.ChooseConcreteOperationFragment;

public class Binaries extends ChooseConcreteOperationFragment {

    public Binaries() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int calcId(int i, int j) {
        return i * 4 + j;
    }

    @Override
    protected String[] getAvailableOperationsForFragment() {
        return getActivity().getResources().getStringArray(R.array.binaryOperations);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int offset() {
        return 0;
    }
}