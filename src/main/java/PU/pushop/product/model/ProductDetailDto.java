package PU.pushop.product.model;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailDto {
    private Long productId;
    private ProductType productType;
    private String productName;
    private Integer price;
    private String productInfo;
    private String manufacturer;
    private Boolean isDiscount;
    private Integer discountRate;
    private Boolean isRecommend;

    public ProductDetailDto(Product product) {
        this(
                product.getProductId(),
                product.getProductType(),
                product.getProductName(),
                product.getPrice(),
                product.getProductInfo(),
                product.getManufacturer(),
                product.getIsDiscount(),
                product.getDiscountRate(),
                product.getIsRecommend()
        );
    }

}
