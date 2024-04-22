package PU.pushop.global.dummydata.util;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.enums.ProductType;
import PU.pushop.product.repository.ProductRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * The ProductDataUtil class is responsible for generating and saving product data. It uses the ProductRepositoryV1
 * interface to save each product to the database. The generated product data is based on the given count parameter.
 */
@Component
@RequiredArgsConstructor
public class ProductDataUtil {

    private final ProductRepositoryV1 productRepository;

    public List<Product> generateProductData(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(this::createProduct)
                // Save each product to the database
                .map(this::saveProduct)
                // List<Product> 변환
                .collect(Collectors.toList());
    }

    private Product createProduct(int index) {
        /**
         * 3 의 배수 : ProductType.UNISEX
         * 나머지 1일 때 : ProductType.MAN
         * 나머지 2일 때 : ProductType.WOMAN
         */
        ProductType productType = getProductType(index);
        // 상품의 각 인자들 설정
        String productName = String.format("Product %d", index);
        String productInfo = String.format("This is a %s product", productType.toString().toLowerCase());
        String manufacturer = String.format("Manufacturer %d", index);
        int price = 10000 * (index % 5 + 1); // 가격 예시
        boolean isDiscount = index % 2 == 0;
        boolean isRecommend = index % 3 == 0;

        return Product.createDummyProduct(productName, productType, price, productInfo, manufacturer, isDiscount, isRecommend);
    }

    private ProductType getProductType(int index) {
        if (index % 3 == 0) return ProductType.UNISEX;
        if (index % 3 == 1) return  ProductType.MAN;

        return ProductType.WOMAN;
    }

    // 단순히 DB 에 저장하는 로직
    private Product saveProduct(Product product) {
        return productRepository.save(product);
    }
}
