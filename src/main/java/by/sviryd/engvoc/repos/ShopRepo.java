package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepo extends JpaRepository<Shop, Long> {
    Shop findByName(String name);
}
