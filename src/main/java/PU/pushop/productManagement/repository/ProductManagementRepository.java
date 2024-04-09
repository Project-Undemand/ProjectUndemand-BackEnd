package PU.pushop.productManagement.repository;

import PU.pushop.productManagement.entity.ProductManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductManagementRepository extends JpaRepository<ProductManagement, Long> {
}
