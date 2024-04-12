package PU.pushop.productManagement.model;

import PU.pushop.category.entity.Category;
import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productManagement.entity.enums.Size;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryCreateDto {
    @NotNull(message = "상품은 필수로 지정해야 합니다.")
    private Long productId;
    @NotNull(message = "색상은 필수로 지정해야 합니다.")
    private Long colorId;
    @NotNull(message = "카테고리는 필수로 지정해야 합니다.")
    private Long categoryId;
    @NotNull(message = "사이즈는 필수로 지정해야 합니다.")
    private Size size;

    private Long initialStock;
    private Long additionalStock;
    private Long productStock;
    private Boolean isRestockAvailable = false;
    private Boolean isRestocked = false;
    private Boolean isSoldOut = false;

    public InventoryCreateDto(ProductManagement productManagement) {
        this(
                productManagement.getProduct().getProductId(),
                productManagement.getColor().getColorId(),
                productManagement.getCategory().getCategoryId(),
                productManagement.getSize(),
                productManagement.getInitialStock(),
                productManagement.getAdditionalStock(),
                productManagement.getProductStock(),
                productManagement.isRestockAvailable(),
                productManagement.isRestocked(),
                productManagement.isSoldOut()
        );
    }

    public static ProductManagement requestForm(InventoryCreateDto request) {
        ProductManagement productManagement = new ProductManagement();
        Product product = new Product();
        product.setProductId(request.getProductId());
        productManagement.setProduct(product);

        ProductColor color = new ProductColor();
        color.setColorId(request.getColorId());
        productManagement.setColor(color);

        Category category = new Category();
        category.setCategoryId(request.getCategoryId());
        productManagement.setCategory(category);

        productManagement.setSize(request.getSize());
        productManagement.setInitialStock(request.getInitialStock());
        productManagement.setAdditionalStock(request.getAdditionalStock());
        productManagement.setProductStock(request.getProductStock());
        productManagement.setRestockAvailable(request.getIsRestockAvailable());
        productManagement.setRestocked(request.getIsRestocked());
        productManagement.setSoldOut(request.getIsSoldOut());

        return productManagement;
    }


}
