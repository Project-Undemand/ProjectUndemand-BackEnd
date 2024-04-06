package PU.pushop.product.model;

import PU.pushop.Inquiry.model.InquiryReplyDto;
import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.WishList;
import PU.pushop.product.entity.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
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
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String manufacturer;

    private boolean isSale;
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
                product.getIsSale(),
                product.getIsRecommend(),
                product.getWishLists().stream().map(WishListDto::new)
                        .collect(Collectors.toList()),
                (long) product.getWishLists().size()
        );
    }

}

