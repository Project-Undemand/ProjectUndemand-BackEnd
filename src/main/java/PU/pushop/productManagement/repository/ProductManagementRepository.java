package PU.pushop.productManagement.repository;

import PU.pushop.category.entity.Category;
import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productManagement.entity.enums.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductManagementRepository extends JpaRepository<ProductManagement, Long> {
    Optional<ProductManagement> findByProductAndColorAndCategoryAndSize(Product product, ProductColor color, Category category, Size size);
}
