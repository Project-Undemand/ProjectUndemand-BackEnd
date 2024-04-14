package PU.pushop.product.repository;

import PU.pushop.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepositoryV1 extends JpaRepository<Product, Long> {

    Optional<Product> findByProductName(String productName);
    Optional<Product> findByProductId(Long productId);
    @Query("SELECT p FROM Product p")
    List<Product> findAllProductsOnly();

}
