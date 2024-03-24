package PU.pushop.product.model;

import PU.pushop.product.entity.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "categoryId")
@AllArgsConstructor
public class ProductCategoryDto {
    private Long categoryId;
    private String category;

    public ProductCategoryDto(ProductCategory category) {
        this(
                category.getCategoryId(),
                category.getCategory()
        );
    }
}
