package PU.pushop.product.repository;

import PU.pushop.product.entity.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductColor, Long> {
    Optional<ProductColor> findByColor(String color);
}
