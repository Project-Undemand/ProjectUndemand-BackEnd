package PU.pushop.wishList.model;

import PU.pushop.product.entity.enums.ProductType;
import PU.pushop.wishList.entity.WishList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishListResponseDto {
    private Long wishListId;
    private Long productId;
    private ProductType productType;
    private String productName;
    private Integer price;
    private String productInfo;
    private Boolean isDiscount;
    private Boolean isRecommend;

    public WishListResponseDto(WishList wishList) {
        this(
                wishList.getWishListId(),
                wishList.getProduct().getProductId(),
                wishList.getProduct().getProductType(),
                wishList.getProduct().getProductName(),
                wishList.getProduct().getPrice(),
                wishList.getProduct().getProductInfo(),
                wishList.getProduct().getIsDiscount(),
                wishList.getProduct().getIsRecommend()
        );
    }
}
