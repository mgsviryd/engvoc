package by.sviryd.engvoc.util;

import java.math.BigDecimal;

public class DoubleUtil {
    public static double round(double value, int places, int roundingMode) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, roundingMode);
        return bd.doubleValue();
    }
    public static double getRatio(double percent) {
        return percent / 100;
    }
}
