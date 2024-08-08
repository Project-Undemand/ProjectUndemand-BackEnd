package PU.pushop.InventoryProduct.controller;

import PU.pushop.InventoryProduct.entity.InventoryProduct;
import PU.pushop.InventoryProduct.repository.InventoryProductRepository;
import PU.pushop.InventoryProduct.service.InventoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryProductRepository inventoryProductRepository;

    // 1. 관리자를 위한 전체 InventoryProduct 리스트를 보는 API
    @GetMapping("/admin/all")
    public ResponseEntity<List<InventoryProduct>> getAllInventoryProducts(HttpServletRequest request) {
        List<InventoryProduct> inventoryProducts = inventoryService.getAllInventoryProducts(request);
        return ResponseEntity.ok(inventoryProducts);
    }

    // 2. 판매자가 자신의 InventoryProduct 리스트를 보는 API
    @GetMapping("/seller/{manufacturer}")
    public ResponseEntity<List<InventoryProduct>> getInventoryProductsByManufacturer(HttpServletRequest request, @PathVariable String manufacturer) {
        List<InventoryProduct> inventoryProducts = inventoryService.getInventoryProductsByManufacturer(request, manufacturer);
        return ResponseEntity.ok(inventoryProducts);
    }

    // 3. InventoryProduct의 productStock를 PUT하는 API
    @PutMapping("/updateStock/{id}")
    public ResponseEntity<InventoryProduct> updateProductStock(@PathVariable Long id, @RequestParam Long productStock) {
        InventoryProduct updatedInventoryProduct = inventoryService.updateProductStock(id, productStock);
        return ResponseEntity.ok(updatedInventoryProduct);
    }
}
