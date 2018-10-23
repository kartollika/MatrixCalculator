package kartollika.matrixcalc;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import kartollika.matrixcalc.activities.PreferenceActivity;
import kartollika.matrixmodules.matrix.AugmentedMatrix;
import kartollika.matrixmodules.matrix.Matrix;

public class MatrixManager {

    private static MatrixManager matrixManager;
    private SharedPreferences preferenceManager;
    private Context context;
    private static Matrix matrixA;
    private static boolean isAEdited = false;

    private static Matrix matrixB;
    private static boolean isBEdited = false;

    private static AugmentedMatrix matrixSystem;
    private static boolean isSystemEdited = false;

    private MatrixManager(Context context) {
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(context);
        matrixA = getDefaultMatrix();
        matrixB = getDefaultMatrix();
        matrixSystem = getDefaultAugmentedMatrix();
        this.context = context;
    }

    public static MatrixManager getInstance(Context context) {
        if (matrixManager == null) {
            matrixManager = new MatrixManager(context);
        }
        return matrixManager;
    }

    public Matrix getA() {
        if (isAEdited) {
            return matrixA;
        } else {
            return getDefaultMatrix();
        }
    }

    public void setA(Matrix a) {
        matrixA = a;
        isAEdited = true;
    }

    public Matrix getB() {
        if (isBEdited) {
            return matrixB;
        } else {
            return getDefaultMatrix();
        }
    }

    public void setB(Matrix b) {
        matrixB = b;
        isBEdited = true;
    }

    private Matrix getDefaultMatrix() {
        return new Matrix(preferenceManager
                .getInt(PreferenceActivity.KEY_DEFAULT_ROWS, 3),
                preferenceManager.getInt(PreferenceActivity.KEY_DEFAULT_COLUMNS, 3));
    }

    private AugmentedMatrix getDefaultAugmentedMatrix() {
        return new AugmentedMatrix(preferenceManager
                .getInt(PreferenceActivity.KEY_DEFAULT_ROWS, 3),
                preferenceManager.getInt(PreferenceActivity.KEY_DEFAULT_COLUMNS, 3));
    }

    public AugmentedMatrix getMatrixSystem() {
        if (isSystemEdited) {
            return matrixSystem;
        } else {
            return getDefaultAugmentedMatrix();
        }
    }

    public void setSystem(AugmentedMatrix system) {
        this.matrixSystem = system;
        isSystemEdited = true;
    }
}
