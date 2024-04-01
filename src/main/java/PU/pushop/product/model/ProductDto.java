package PU.pushop.product.model;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = "productId")
@AllArgsConstructor
public class ProductDto {

    private Long productId;
    ProductType productType;
    private String productName;
    private Integer price;
    private String productInfo;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String manufacturer;

    private boolean isSale;
    private boolean isRecommend;


    public ProductDto(Product product) {
        this(
                product.getProductId(),
                product.getProductType(),
                product.getProductName(),
                product.getPrice(),
                product.getProductInfo(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getManufacturer(),
                product.getIsSale(),
                product.getIsRecommend()
        );
    }

}

