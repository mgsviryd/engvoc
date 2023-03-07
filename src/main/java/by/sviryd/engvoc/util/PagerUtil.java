package by.sviryd.engvoc.util;

public class PagerUtil {
    public static int getCountPages(int batchSize, long count) {
        long remainder = count % batchSize;
        int countPages;
        if (remainder > 0) {
            countPages = (int) (count / batchSize) + 1;
        } else {
            countPages = (int) (count / batchSize);
        }
        return countPages;
    }
}
