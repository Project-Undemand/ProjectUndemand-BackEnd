package PU.pushop.wishList.model;

import PU.pushop.product.entity.enums.ProductType;
import PU.pushop.productThumbnail.entity.ProductThumbnail;
import PU.pushop.wishList.entity.WishList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private List<String> productThumbnails;

    public WishListResponseDto(WishList wishList) {
        this(
                wishList.getWishListId(),
                wishList.getProduct().getProductId(),
                wishList.getProduct().getProductType(),
                wishList.getProduct().getProductName(),
                wishList.getProduct().getPrice(),
                wishList.getProduct().getProductInfo(),
                wishList.getProduct().getIsDiscount(),
                wishList.getProduct().getIsRecommend(),
                wishList.getProduct().getProductThumbnails().stream().map(ProductThumbnail::getImagePath).toList()
        );
    }
}
