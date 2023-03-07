package by.sviryd.engvoc.util;

import org.junit.Test;

import java.util.stream.IntStream;

public class StreamParallelIT {
    @Test
    public void parallelOrderExecution(){
        IntStream.iterate(0, x-> x+1).limit(5000).parallel().forEach(System.out::println);
        System.out.println("END");
    }
}
