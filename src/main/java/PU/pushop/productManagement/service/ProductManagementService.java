package PU.pushop.productManagement.service;

import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productManagement.model.InventoryUpdateDto;
import PU.pushop.productManagement.repository.ProductManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static PU.pushop.global.ResponseMessageConstants.PRODUCT_NOT_FOUND;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ProductManagementService {
    public final ProductManagementRepository productManagementRepository;


    /**
     * 상품관리 등록
     * @param request
     * @return
     */
    public Long createInventory(ProductManagement request) {

        ProductManagement existingInventory = productManagementRepository.findByProductAndColorAndCategoryAndSize(request.getProduct(), request.getColor(), request.getCategory(), request.getSize()).orElse(null);

        if (existingInventory != null) {
            throw new IllegalArgumentException("이미 존재하는 상품입니다.");
        }

        productManagementRepository.save(request);
        return request.getInventoryId();
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

    public ProductManagement updateInventory(Long inventoryId, InventoryUpdateDto updatedInventory) {

        ProductManagement existingInventory = productManagementRepository.findById(inventoryId)
                .orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));

        InventoryUpdateDto.updateInventoryForm(existingInventory, updatedInventory);

        return productManagementRepository.save(existingInventory);
    }

    /**
     * 상품 관리 삭제
     * @param inventoryId
     */
    public void deleteInventory(Long inventoryId) {
        ProductManagement existingInventory = productManagementRepository.findById(inventoryId)
                .orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));
        productManagementRepository.delete(existingInventory);

    }
}
