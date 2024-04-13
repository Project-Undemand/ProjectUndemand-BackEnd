package PU.pushop.product.model;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.enums.ProductType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateDto {
    @NotBlank(message = "상품 이름은 필수입니다.")
    private String productName;
    private ProductType productType;
    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;
    private String productInfo;
    private String manufacturer;
    private Boolean isSale = false;
    private Boolean isRecommend = false;


    public ProductCreateDto(Product product) {
        this(
                product.getProductName(),
                product.getProductType(),
                product.getPrice(),
                product.getProductInfo(),
                product.getManufacturer(),
                product.getIsSale(),
                product.getIsRecommend()
        );
    }

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
