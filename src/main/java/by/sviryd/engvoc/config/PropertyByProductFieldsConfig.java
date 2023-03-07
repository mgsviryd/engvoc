package by.sviryd.engvoc.config;

import by.sviryd.engvoc.domain.Property;
import by.sviryd.engvoc.type.PropertyType;
import by.sviryd.engvoc.type.Unit;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "property.by-product-fields")
public class PropertyByProductFieldsConfig {
    private List<String> names = new ArrayList<>();
    private List<String> paths = new ArrayList<>();
    private List<String> types = new ArrayList<>();
    private List<String> units = new ArrayList<>();

    public List<Property> getPropertiesByProductFields() {
        List<Property> list = new ArrayList<>();
        int size = names.size();
        for (int i = 0; i < size; i++) {
            list.add(new Property(
                    names.get(i),
                    paths.get(i),
                    PropertyType.valueOf(types.get(i)),
                    Unit.valueOf(units.get(i))
            ));
        }
        return list;
    }
}
