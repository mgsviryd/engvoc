package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.ProductProperty;
import by.sviryd.engvoc.domain.ProductPropertyId;
import by.sviryd.engvoc.domain.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductPropertyRepo extends JpaRepository<ProductProperty, ProductPropertyId>, ProductPropertyCustomRepo {
    @Query("select d.property, d.propertyData from ProductProperty d where d.property in :properties GROUP BY d.property.id, d.propertyData order by d.property.id ASC, d.propertyData ASC")
    List<Object[]> getProductPropertyData(@Param("properties") List<Property> properties);
}
