package kartollika.matrixcalc.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import kartollika.matrixcalc.R;

public class Binaries extends ChooseConcreteOperationFragment {

    public Binaries() {
        super();
    }

    public static Binaries newInstance() {

        Bundle args = new Bundle();

        Binaries fragment = new Binaries();
        fragment.setArguments(args);
        return fragment;
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
        return requireContext().getResources().getStringArray(R.array.binaryOperations);
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
