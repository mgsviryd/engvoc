package by.sviryd.engvoc.util;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Exception;
}
