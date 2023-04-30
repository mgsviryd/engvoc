package by.sviryd.engvoc.gson;

import by.sviryd.engvoc.domain.User;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class GsonExcludeStrategies {
    ExclusionStrategy userOnlyInfo = new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes field) {
            String[] excludeFields = new String[]{"password", "sub"};
            return someFieldExcluded(field, User.class, excludeFields);
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    };

    private boolean someFieldExcluded(FieldAttributes field, Class clazz, String[] excludeFields) {
        for (String excludeField : excludeFields) {
            if (field.getDeclaringClass() == clazz && field.getName().equals(excludeField)) {
                return true;
            }
        }
        return false;
    }
}
