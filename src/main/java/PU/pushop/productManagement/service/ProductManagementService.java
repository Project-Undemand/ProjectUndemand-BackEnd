package PU.pushop.productManagement.service;

import PU.pushop.category.entity.Category;
import PU.pushop.category.repository.CategoryRepository;
import PU.pushop.global.authorization.RequiresRole;
import PU.pushop.members.entity.enums.MemberRole;
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
    public final CategoryRepository categoryRepository;


    /**
     * 상품관리 등록
     * @param request
     * @return
     */
    @RequiresRole({MemberRole.ADMIN, MemberRole.SELLER})
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

    /**
     * 상품 관리 수정
     * @param inventoryId
     * @param request
     * @return
     */
    @RequiresRole({MemberRole.ADMIN, MemberRole.SELLER})
    public ProductManagement updateInventory(Long inventoryId, InventoryUpdateDto request) {

        ProductManagement existingInventory = productManagementRepository.findById(inventoryId)
                .orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));

        Long productStock = existingInventory.getProductStock() + request.getAdditionalStock();
        Category category = categoryRepository.findByCategoryId(request.getCategoryId()).orElseThrow(() -> new NoSuchElementException("카테고리를 찾을 수 없습니다."));

        existingInventory.updateInventory(category, request.getAdditionalStock(), productStock, request.getIsRestockAvailable(), request.getIsRestocked(),request.getIsSoldOut());

//        InventoryUpdateDto.updateInventoryForm(existingInventory, request);

        return productManagementRepository.save(existingInventory);
    }

    /**
     * 상품 관리 삭제
     * @param inventoryId
     */
    @RequiresRole({MemberRole.ADMIN, MemberRole.SELLER})
    public void deleteInventory(Long inventoryId) {
        ProductManagement existingInventory = productManagementRepository.findById(inventoryId)
                .orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));
        productManagementRepository.delete(existingInventory);

    }
}
