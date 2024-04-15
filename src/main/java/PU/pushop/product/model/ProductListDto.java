package PU.pushop.product.model;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.enums.ProductType;
import PU.pushop.wishList.entity.WishList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductListDto {
    private Long productId;
    private ProductType productType;
    private String productName;
    private Integer price;
    private LocalDateTime createdAt;
    private Long wishListCount;
    private Boolean isDiscount;
    private Integer discountRate;
    private Boolean isRecommend;

    public ProductListDto(Product product) {
        this(
                product.getProductId(),
                product.getProductType(),
                product.getProductName(),
                product.getPrice(),
                product.getCreatedAt(),
                product.getWishListCount(),
                product.getIsDiscount(),
                product.getDiscountRate(),
                product.getIsRecommend()
        );
    }
}
