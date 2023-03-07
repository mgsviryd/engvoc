package by.sviryd.engvoc.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/*
https://stackoverflow.com/questions/23837561/jpa-2-0-many-to-many-with-extra-column
 */
@ToString(of = {"product", "property"})
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "product_property")
public class ProductProperty implements INamePath, Serializable {
    public static final long serialVersionUID = 1L;
    @EmbeddedId
    private ProductPropertyId id = new ProductPropertyId();

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @MapsId("propertyId")
    @JoinColumn(name = "property_id")
    private Property property;

    @Embedded
    private PropertyData propertyData;

    private Integer priority;

    private boolean invisible;

    @Override
    public String getName() {
        return propertyData.getName();
    }

    @Override
    public void setName(String name) {
        propertyData.setName(name);
    }

    @Override
    public String getPath() {
        return propertyData.getPath();
    }

    @Override
    public void setPath(String path) {
        propertyData.setPath(path);
    }
}

