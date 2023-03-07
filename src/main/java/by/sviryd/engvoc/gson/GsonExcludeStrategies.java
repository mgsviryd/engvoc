package by.sviryd.engvoc.gson;

import by.sviryd.engvoc.domain.Category;
import by.sviryd.engvoc.domain.Property;
import by.sviryd.engvoc.domain.User;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class GsonExcludeStrategies {
    ExclusionStrategy categoryWithoutPropertiesAndProducts = new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes field) {
            String[] excludeFields = new String[]{"properties", "products"};
            return someFieldExcluded(field, Category.class, excludeFields);
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    };
    ExclusionStrategy propertyWithoutCategory = new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes field) {
            String[] excludeFields = new String[]{"category"};
            return someFieldExcluded(field, Property.class, excludeFields);
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    };
    ExclusionStrategy userOnlyInfo = new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes field) {
            String[] excludeFields = new String[]{"password", "sub", "messages", "subscribers", "subscriptions"};
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
