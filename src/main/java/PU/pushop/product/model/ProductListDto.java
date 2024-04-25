package PU.pushop.product.model;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.enums.ProductType;
import PU.pushop.productThumbnail.entity.ProductThumbnail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<String> productThumbnails; // 썸네일 리스트 추가

    // 필터링 테스트용, 추후 삭제
    private List<String> category;
    private List<Long> categoryId;

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
                product.getIsRecommend(),
                product.getProductThumbnails().stream().map(ProductThumbnail::getImagePath).toList(), // 경로만 가져오기

                // 필터링 테스트용, 추후 삭제
                product.getProductManagements() != null && !product.getProductManagements().isEmpty() ? product.getProductManagements().stream().map(productManagement -> productManagement.getCategory().getName()).toList() : null,
                product.getProductManagements() != null && !product.getProductManagements().isEmpty() ? product.getProductManagements().stream().map(productManagement -> productManagement.getCategory().getCategoryId()).toList() : null




        );
    }
}
