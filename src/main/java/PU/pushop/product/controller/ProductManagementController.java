package PU.pushop.product.controller;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductCategory;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.product.entity.ProductManagement;
import PU.pushop.product.entity.enums.Size;
import PU.pushop.product.model.ProductManagementDto;
import PU.pushop.product.service.ProductManagementService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class ProductManagementController {
    private final ProductManagementService managementService;

    // Request Data
    @Data
    static class InventoryRequest {
        private Long productId;
        private Long colorId;
        private Long categoryId;

        private Size size;

        private Long initialStock;
        private Long additionalStock;
        private Long productStock;
    }

    private ProductManagement InventoryFormRequest(InventoryRequest request) {
        ProductManagement productManagement = new ProductManagement();
        Product product = new Product();
        product.setProductId(request.getProductId());
        productManagement.setProduct(product);

        ProductColor color = new ProductColor();
        color.setColorId(request.getColorId());
        productManagement.setColor(color);

        ProductCategory category = new ProductCategory();
        category.setCategoryId(request.getCategoryId());
        productManagement.setCategory(category);

        productManagement.setSize(request.getSize());
        productManagement.setInitialStock(request.getInitialStock());
        productManagement.setAdditionalStock(request.getAdditionalStock());
        productManagement.setProductStock(request.getProductStock());

        return productManagement;

    }

    @Data
    private class UpdateResponse {
        private Long inventoryId;
        private Product productId;

        private UpdateResponse(Long inventoryId, Product productId) {
            this.inventoryId = inventoryId;
            this.productId = productId;
        }
    }

    /**
     * 전체 상품 관리 조회
     * @return
     */
    @GetMapping("")
    public ResponseEntity<List<ProductManagementDto>> inventoryList() {
        List<ProductManagement> inventoryList = managementService.allInventory();
        List<ProductManagementDto> collet = inventoryList.stream()
                .map(ProductManagementDto::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(collet, HttpStatus.OK);
    }

    /**
     * 상품 관리 id로 조회
     * @param inventoryId
     * @return
     */
    @GetMapping("/{inventoryId}")
    public ResponseEntity<ProductManagementDto> getInventoryById(@PathVariable Long inventoryId) {
        ProductManagement inventoryDetail = managementService.inventoryDetail(inventoryId);
        ProductManagementDto productManagementDto = new ProductManagementDto(inventoryDetail);
        return new ResponseEntity<>(productManagementDto, HttpStatus.OK);
    }


    /**
     * 상품 관리 등록
     * @param request
     * @return
     */
    @PostMapping("/new")
    public ResponseEntity<?> createInventory(@Valid @RequestBody InventoryRequest request) {


        ProductManagement productManagement = InventoryFormRequest(request);
        Long createdId = managementService.createInventory(productManagement);
        return ResponseEntity.ok(createdId);
    }

    /**
     * 상품 관리 수정
     * @param inventoryId
     * @param request
     * @return
     */
    @PutMapping("/{inventoryId}")
    public ResponseEntity<?> updateInventory(@PathVariable Long inventoryId, @Valid @RequestBody InventoryRequest request) {
        ProductManagement updatedInventory = InventoryFormRequest(request);
        ProductManagement updated = managementService.updateInventory(inventoryId, updatedInventory);
        UpdateResponse response = new UpdateResponse(updated.getInventoryId(), updated.getProduct());

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    /**
     * 상품 관리 삭제
     * @param inventoryId
     * @return
     */
    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<?> deleteInventory(@PathVariable Long inventoryId) {
        managementService.deleteInventory(inventoryId);
        return ResponseEntity.ok().build();
    }
}
