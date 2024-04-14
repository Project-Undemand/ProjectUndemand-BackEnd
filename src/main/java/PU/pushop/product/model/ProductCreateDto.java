package PU.pushop.product.model;

import PU.pushop.global.validation.ValidDiscountRate;
import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.enums.ProductType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidDiscountRate // 커스텀 유효성 검사 애노테이션 적용
public class ProductCreateDto {
    @NotBlank(message = "상품 이름은 필수입니다.")
    private String productName;
    private ProductType productType;
    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;
    private String productInfo;
    private String manufacturer;
    private Boolean isDiscount = false;
    @Max(value = 100, message = "할인율은 100을 초과할 수 없습니다.")
    private Integer discountRate = null;
    private Boolean isRecommend = false;


    public ProductCreateDto(Product product) {
        this(
                product.getProductName(),
                product.getProductType(),
                product.getPrice(),
                product.getProductInfo(),
                product.getManufacturer(),
                product.getIsDiscount(),
                product.getDiscountRate(),
                product.getIsRecommend()
        );
    }

    // isDiscount가 false 라면 할인율 null
    public void setIsDiscount(Boolean isDiscount) {
        this.isDiscount = isDiscount;
        if (Boolean.FALSE.equals(isDiscount)) {
            this.discountRate = null;
        }
    }

    // discountRate를 설정하는 메서드
//    public void setDiscountRate(Integer discountRate) {
//        // isDiscount가 false이면서 discountRate가 설정되면 isDiscount를 true로 변경
//        if (discountRate != null && discountRate > 0) {
//            this.isDiscount = true;
//        }
//        this.discountRate = discountRate;
//    }

/*  Model Mapper 사용으로 필요 없어짐

    public static Product requestForm(ProductCreateDto request) {
        Product product = new Product();

        product.setProductName(request.getProductName());
        product.setProductType(request.getProductType());
        product.setPrice(request.getPrice());
        product.setProductInfo(request.getProductInfo());
        product.setManufacturer(request.getManufacturer());
        product.setIsSale(request.getIsSale());
        product.setIsRecommend(request.getIsRecommend());
        return product;
    }
*/

    /* Model Mapper 사용으로 필요 없어짐

        public static Product updateForm(Product existingProduct, Product updatedProduct) {
        // 기존 상품 정보 업데이트
        existingProduct.setProductName(updatedProduct.getProductName());
        existingProduct.setProductType(updatedProduct.getProductType());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setProductInfo(updatedProduct.getProductInfo());
        existingProduct.setManufacturer(updatedProduct.getManufacturer());
        existingProduct.setIsSale(updatedProduct.getIsSale());
        existingProduct.setIsRecommend(updatedProduct.getIsRecommend());

        return existingProduct;
    }*/

}
