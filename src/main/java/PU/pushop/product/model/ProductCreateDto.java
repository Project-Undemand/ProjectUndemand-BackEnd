package PU.pushop.product.model;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateDto {
    private String productName;
    private ProductType productType;
    private Integer price;
    private String productInfo;
    private String manufacturer;

    public ProductCreateDto(Product product) {
        this(
                product.getProductName(),
                product.getProductType(),
                product.getPrice(),
                product.getProductInfo(),
                product.getManufacturer()
        );
    }

    public static Product requestForm(ProductCreateDto request) {
        Product product = new Product();

        product.setProductName(request.getProductName());
        product.setProductType(request.getProductType());
        product.setPrice(request.getPrice());
        product.setProductInfo(request.getProductInfo());
        product.setManufacturer(request.getManufacturer());
        return product;
    }

}
