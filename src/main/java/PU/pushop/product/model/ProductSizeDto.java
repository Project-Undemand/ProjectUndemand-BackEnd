package PU.pushop.product.model;

import PU.pushop.product.entity.ProductSize;
import PU.pushop.product.entity.enums.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "sizeId")
@AllArgsConstructor
public class ProductSizeDto {
    private Long sizeId;
    Size size;

    public ProductSizeDto(ProductSize size) {
        this(
                size.getSizeId(),
                size.getSize()
        );
    }
}
