package PU.pushop.productManagement.model;

import PU.pushop.category.entity.Category;
import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.productManagement.controller.ProductManagementController;
import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productManagement.entity.enums.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryCreateDto {
    private Long productId;
    private Long colorId;
    private Long categoryId;

    private Size size;

    private Long initialStock;
    private Long additionalStock;
    private Long productStock;

    public InventoryCreateDto(ProductManagement productManagement) {
        this(
                productManagement.getProduct().getProductId(),
                productManagement.getColor().getColorId(),
                productManagement.getCategory().getCategoryId(),
                productManagement.getSize(),
                productManagement.getInitialStock(),
                productManagement.getAdditionalStock(),
                productManagement.getProductStock()
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

        return productManagement;
    }


}
