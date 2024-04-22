package PU.pushop.productManagement.model;

import PU.pushop.category.entity.Category;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productManagement.entity.enums.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class InventoryUpdateDto {
    private Long colorId;
    private Long categoryId;
    private Size size;
//    private Long initialStock; // 수정할 땐 초기 재고 수정 불가
    private Long additionalStock;
    private Long productStock;
    private Boolean isRestockAvailable = false;
    private Boolean isRestocked = false;
    private Boolean isSoldOut = false;

    public InventoryUpdateDto(ProductManagement productManagement) {
        this(
                productManagement.getColor().getColorId(),
                productManagement.getCategory().getCategoryId(),
                productManagement.getSize(),
                productManagement.getAdditionalStock(),
                productManagement.getProductStock(),
                productManagement.isRestockAvailable(),
                productManagement.isRestocked(),
                productManagement.isSoldOut()
        );
    }

    public static ProductManagement updateInventoryForm(ProductManagement existingInventory, InventoryUpdateDto request) {

        existingInventory.setProduct(existingInventory.getProduct());

        ProductColor color = new ProductColor();
        color.setColorId(request.getColorId());
        existingInventory.setColor(color);

        Category category = new Category();
        category.setCategoryId(request.getCategoryId());
        existingInventory.setCategory(category);
        existingInventory.setSize(request.getSize());
        existingInventory.setAdditionalStock(request.getAdditionalStock());
        existingInventory.setProductStock(existingInventory.getInitialStock() + request.getAdditionalStock()); // 상품 재고는 초기재고 + 추가재고로 자동 설정
        existingInventory.setRestockAvailable(request.getIsRestockAvailable());
        existingInventory.setRestocked(request.getIsRestocked());
        existingInventory.setSoldOut(request.getIsSoldOut());

        return existingInventory;

    }
}
