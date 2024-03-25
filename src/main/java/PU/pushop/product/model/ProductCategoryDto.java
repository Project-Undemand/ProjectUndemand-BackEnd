package PU.pushop.product.model;

import PU.pushop.product.entity.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(of = "categoryId")
@AllArgsConstructor
public class ProductCategoryDto {
    private Long categoryId;
    private String name;
    private Long depth;
    private List<ProductCategoryDto> children;

    public static ProductCategoryDto of (ProductCategory category) {
        return new ProductCategoryDto(
                category.getCategoryId(),
                category.getName(),
                category.getDepth(),
                category.getChildren().stream().map(ProductCategoryDto::of).collect(Collectors.toList())
        );

    }
}
