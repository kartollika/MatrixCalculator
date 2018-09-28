package kartollika.matrixcalc.utilities;

import kartollika.matrixcalc.App;

public final class InterstitialShow {

    private static final int OPERATION_UNTIL_AD = 3;
    public static int CUR_OPERATIONS;

    public static void showInterstitialAd() {
        if (App.canShowNewInterstitialVideo()) {
            if (CUR_OPERATIONS + 1 >= OPERATION_UNTIL_AD) {
                CUR_OPERATIONS = 0;
                AdUtils.showInterstitialAd();
            } else {
                CUR_OPERATIONS++;
            }
        } else {
            CUR_OPERATIONS = 1;
        }
    }

    public static int getCurOperations() {
        return CUR_OPERATIONS;
    }
}
