package by.sviryd.engvoc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectToViewTransformUtil implements Serializable {
    public static <T> T transformToView(T object, Class viewClass) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String result = mapper
                    .writerWithView(viewClass)
                    .writeValueAsString(object);
            return mapper
                    .readerWithView(viewClass)
                    .forType(object.getClass())
                    .readValue(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> transformToView(Class viewClass, List<T> objects) {
        if (objects == null || objects.isEmpty()) return objects;
        return objects.stream().map(x->transformToView(x,viewClass)).collect(Collectors.toList());
    }
}
