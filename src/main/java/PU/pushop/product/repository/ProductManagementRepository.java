package PU.pushop.product.repository;

import PU.pushop.product.entity.ProductManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductManagementRepository extends JpaRepository<ProductManagement, Long> {
}
