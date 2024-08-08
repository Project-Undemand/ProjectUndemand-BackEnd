package PU.pushop.InventoryProduct.repository;

import PU.pushop.InventoryProduct.entity.InventoryProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryProductRepository extends JpaRepository<InventoryProduct, Long> {

    @Query("SELECT ip.productCode FROM InventoryProduct ip WHERE ip.productCode LIKE CONCAT(:parentCode, '_', :subCode, '_%') ORDER BY ip.productCode DESC")
    List<String> findProductCodes(String parentCode, String subCode);

    boolean existsByProductCode(String productCode);

    List<InventoryProduct> findByProduct_Manufacturer(String manufacturer);
}
