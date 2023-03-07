package by.sviryd.engvoc.domain;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Embeddable
public class PropertyData implements INamePath {
    @Column(length = 500)
    @Length(max = 500)
    private String name;

    @Column(length = 600)
    @Length(max = 600)
    private String path;

    private Double doubleData;
    private Integer integerData;
    private boolean booleanData;

    public PropertyData(String name, String path) {
        this.name = name;
        this.path = path;
    }
    public PropertyData(String path){
        this.path = path;
    }
}
