package PU.pushop.product.model;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.enums.ProductType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateDto {
    @NotBlank(message = "상품 이름은 필수입니다.")
    private String productName;
    private ProductType productType;
    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;
    private String productInfo;
    private String manufacturer;

    public ProductCreateDto(Product product) {
        this(
                product.getProductName(),
                product.getProductType(),
                product.getPrice(),
                product.getProductInfo(),
                product.getManufacturer()
        );
    }

    public static Product requestForm(ProductCreateDto request) {
        Product product = new Product();

        product.setProductName(request.getProductName());
        product.setProductType(request.getProductType());
        product.setPrice(request.getPrice());
        product.setProductInfo(request.getProductInfo());
        product.setManufacturer(request.getManufacturer());
        return product;
    }

}
