package PU.pushop.productManagement.service;

import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productManagement.repository.ProductManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductManagementService {
    public final ProductManagementRepository productManagementRepository;


    /**
     * 상품관리 등록
     *
     * @param productManagement
     * @return
     */
    @Transactional
    public Long createInventory(ProductManagement productManagement) {
        productManagementRepository.save(productManagement);
        return productManagement.getInventoryId();
    }

    /**
     * 상품 관리 정보 조회 - 상품 하나 id로 찾기
     * @param inventoryId
     * @return
     */
    public ProductManagement inventoryDetail(Long inventoryId) {
        return productManagementRepository.findById(inventoryId).get();
    }

    /**
     * 상품 관리 정보 조회 - 모든 상품 찾기
     * @return
     */
    public List<ProductManagement> allInventory() {
        return productManagementRepository.findAll();
    }

    public ProductManagement updateInventory(Long inventoryId, ProductManagement updatedInventory) {
        ProductManagement existingInventory = productManagementRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        // 기존 상품 관리 업데이트
        existingInventory.setProduct(updatedInventory.getProduct());
        existingInventory.setColor(updatedInventory.getColor());
        existingInventory.setProductStock(updatedInventory.getProductStock());
        existingInventory.setCategory(updatedInventory.getCategory());
        existingInventory.setSize(updatedInventory.getSize());
        existingInventory.setAdditionalStock(updatedInventory.getAdditionalStock());
        existingInventory.setInitialStock(updatedInventory.getInitialStock());
        existingInventory.setRestockAvailable(updatedInventory.isRestockAvailable());
        existingInventory.setRestocked(updatedInventory.isRestocked());
        existingInventory.setSoldOut(updatedInventory.isSoldOut());

        return productManagementRepository.save(existingInventory);
    }

    /**
     * 상품 관리 삭제
     * @param inventoryId
     */
    public void deleteInventory(Long inventoryId) {
        ProductManagement existingInventory = productManagementRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        productManagementRepository.delete(existingInventory);

    }
}
