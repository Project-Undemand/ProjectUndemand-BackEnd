package PU.pushop.product.model;

import PU.pushop.product.entity.ProductColor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "colorId")
@AllArgsConstructor
public class ProductColorDto {
    private Long colorId;
    private String color;

    public ProductColorDto(ProductColor color) {
        this(
                color.getColorId(),
                color.getColor()
        );
    }
}
