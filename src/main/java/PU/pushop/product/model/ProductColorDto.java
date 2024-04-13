package PU.pushop.product.model;

import PU.pushop.product.entity.ProductColor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "colorId")
@AllArgsConstructor
public class ProductColorDto {
    private Long colorId;
    @NotBlank(message = "색상 이름은 필수입니다.")
    private String color;

    public ProductColorDto(ProductColor color) {
        this(
                color.getColorId(),
                color.getColor()
        );
    }

    public ProductColorDto() {

    }
}
