package PU.pushop.product.model;

import PU.pushop.product.entity.ProductManagement;
import PU.pushop.product.entity.enums.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
public class ProductManagementDto {
    private Long inventoryId;
    private Long productId; // Product 테이블의 pk 참조
    private Long colorId; // ProductColor 테이블의 pk 참조
    private Long categoryId; // ProductCategory 테이블의 pk 참조
    Size size; //enum
    private Long initialStock;
    private Long additionalStock;
    private Long productStock;
    private boolean isSoldOut;
    private boolean isRestockAvailable;
    private boolean isRestocked;


    public ProductManagementDto(ProductManagement productManagement) {
        this(
                productManagement.getInventoryId(),
                productManagement.getProduct().getProductId(),
                productManagement.getColor().getColorId(),
                productManagement.getCategory().getCategoryId(),
                productManagement.getSize(),
                productManagement.getInitialStock(),
                productManagement.getAdditionalStock(),
                productManagement.getProductStock(),
                productManagement.isSoldOut(),
                productManagement.isRestockAvailable(),
                productManagement.isRestocked()

        );
    }
}
