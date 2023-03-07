package by.sviryd.engvoc.util;

@FunctionalInterface
public interface ThrowingRunnable<T> {
    void run() throws Exception;
}
