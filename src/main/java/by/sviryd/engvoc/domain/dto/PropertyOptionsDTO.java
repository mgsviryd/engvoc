package by.sviryd.engvoc.domain.dto;

import by.sviryd.engvoc.domain.Property;
import by.sviryd.engvoc.domain.PropertyData;
import by.sviryd.engvoc.type.PropertyType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ToString
@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyOptionsDTO {
    public static List<PropertyOptionsDTO> adjustMinMax(List<PropertyOptionsDTO> choicesDTOS) {
        for (PropertyOptionsDTO dto : choicesDTOS) {
            if (dto.getProperty().getType() == PropertyType.INTEGER) {
                PropertyData min = null;
                PropertyData max = null;
                for (PropertyData data : dto.getChoices()) {
                    if (min == null) {
                        min = data;
                    } else if (data.getIntegerData() < min.getIntegerData()) {
                        min = data;
                    }
                    if (max == null) {
                        max = data;
                    } else if (data.getIntegerData() > max.getIntegerData()) {
                        max = data;
                    }
                }
                dto.setMin(min);
                dto.setMax(max);
            }
            if (dto.getProperty().getType() == PropertyType.DOUBLE) {
                PropertyData min = null;
                PropertyData max = null;
                for (PropertyData data : dto.getChoices()) {
                    if (min == null) {
                        min = data;
                    } else if (data.getDoubleData() < min.getDoubleData()) {
                        min = data;
                    }
                    if (max == null) {
                        max = data;
                    } else if (data.getDoubleData() > max.getDoubleData()) {
                        max = data;
                    }
                }
                dto.setMin(min);
                dto.setMax(max);
            }
        }
        return choicesDTOS;
    }

    private Property property;
    private Set<PropertyData> choices = new HashSet<>();
    private PropertyData min = null;
    private PropertyData max = null;

    public PropertyOptionsDTO(Property property) {
        this.property = property;
    }

    public void addChoice(PropertyData propertyData) {
        choices.add(propertyData);
    }

    public boolean isEmpty() {
        return choices.isEmpty();
    }
}
