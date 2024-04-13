package PU.pushop.product.model;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProductDetailDto {
    private Long productId;
    private ProductType productType;
    private String productName;
    private Integer price;
    private String productInfo;
    private String manufacturer;
    private Boolean isSale;
    private Boolean isRecommend;

    public ProductDetailDto(Product product) {
        this(
                product.getProductId(),
                product.getProductType(),
                product.getProductName(),
                product.getPrice(),
                product.getProductInfo(),
                product.getManufacturer(),
                product.getIsSale(),
                product.getIsRecommend()
        );
    }

}
