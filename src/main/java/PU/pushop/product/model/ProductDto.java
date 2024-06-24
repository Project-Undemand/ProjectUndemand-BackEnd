package PU.pushop.product.model;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.enums.ProductType;
import PU.pushop.wishList.model.WishListDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(of = "productId")
@AllArgsConstructor
public class ProductDto {

    private Long productId;
    ProductType productType;
    private String productName;
    private Integer price;
    private String productInfo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String manufacturer;

    private boolean isDiscount;
    private Integer discountRate;
    private boolean isRecommend;

    private List<WishListDto> wishLists;
    private Long wishListCount;


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
                product.getIsDiscount(),
                product.getDiscountRate(),
                product.getIsRecommend(),
                product.getWishLists() != null ? product.getWishLists().stream().map(WishListDto::new).collect(Collectors.toList()) : Collections.emptyList(),
                product.getWishListCount()
        );
    }

}

