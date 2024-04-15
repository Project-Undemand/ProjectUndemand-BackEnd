package PU.pushop.productManagement.controller;

import PU.pushop.product.entity.Product;
import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productManagement.model.InventoryCreateDto;
import PU.pushop.productManagement.model.InventoryUpdateDto;
import PU.pushop.productManagement.model.ProductManagementDto;
import PU.pushop.productManagement.service.ProductManagementService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static PU.pushop.global.ResponseMessageConstants.DELETE_SUCCESS;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class ProductManagementController {
    private final ProductManagementService managementService;


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
     * @param requestDto
     * @return
     */
    @PostMapping("/new")
    public ResponseEntity<Long> createInventory(@Valid @RequestBody InventoryCreateDto requestDto) {
        ProductManagement request = InventoryCreateDto.requestForm(requestDto);
        Long createdId = managementService.createInventory(request);
        return ResponseEntity.ok(createdId);
    }

    /**
     * 상품 관리 수정
     * @param inventoryId
     * @param request
     * @return
     */
    @PutMapping("/{inventoryId}")
    public ResponseEntity<String> updateInventory(@PathVariable Long inventoryId, @Valid @RequestBody InventoryUpdateDto request) {

        ProductManagement updated = managementService.updateInventory(inventoryId, request);
        UpdateResponse response = new UpdateResponse(updated.getInventoryId(), updated.getProduct());

        return ResponseEntity.ok().body("수정 완료");

    }

    /**
     * 상품 관리 삭제
     * @param inventoryId
     * @return
     */
    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<String> deleteInventory(@PathVariable Long inventoryId) {
        managementService.deleteInventory(inventoryId);
        return ResponseEntity.ok().body(DELETE_SUCCESS);
    }
}
