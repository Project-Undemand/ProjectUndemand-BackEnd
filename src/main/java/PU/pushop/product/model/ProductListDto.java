package PU.pushop.product.model;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.enums.ProductType;
import PU.pushop.wishList.entity.WishList;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ProductListDto {
    private Long productId;
    private ProductType productType;
    private String productName;
    private Integer price;
    private LocalDateTime createAt;
    private Long wishListCount;
    private Boolean isSale;
    private Boolean isRecommend;

    public ProductListDto(Product product) {
        this(
                product.getProductId(),
                product.getProductType(),
                product.getProductName(),
                product.getPrice(),
                product.getCreatedAt(),
//                product.getWishLists().stream().map(WishList::getWishListId).collect(Collectors.toList()),
                product.getWishListCount(),
                product.getIsSale(),
                product.getIsRecommend()
        );
    }
}
