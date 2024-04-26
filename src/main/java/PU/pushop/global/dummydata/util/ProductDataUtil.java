package PU.pushop.global.dummydata.util;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.enums.ProductType;
import PU.pushop.product.repository.ProductRepositoryV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
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
@Slf4j
public class ProductDataUtil {

    private final ProductRepositoryV1 productRepository;

    public static final String PRODUCT_NAME_TEMPLATE = "Product %d";
    public static final String PRODUCT_INFO_TEMPLATE = "This is a %s product";
    public static final String MANUFACTURER_NAME_TEMPLATE = "Manufacturer %d";

//    public List<Product> generateProductData(int count) {
//        return IntStream.rangeClosed(1, count)
//                .mapToObj(this::createProduct)
//                .map(this::saveProduct)
//                .collect(Collectors.toList());
//    }

//    private Product createProduct(int index) {
//        ProductType productType = getProductType(index);
//        String productName = buildProductName(index);
//        String productInfo = buildProductInfo(productType);
//        String manufacturer = buildManufacturerName(index);
//        int price = computePrice(index);
//        boolean isDiscount = isDiscount(index);
//        boolean isRecommend = isRecommend(index);
//        return Product.createDummyProduct(productName, productType, price, productInfo, manufacturer, isDiscount, isRecommend);
//    }

    public List<Product> generateProductDataWithImages(List<String> imagePaths) {
        return imagePaths.stream()
                .map(this::createProductFromImagePath)
                .map(this::saveProduct)
                .collect(Collectors.toList());
    }


    private Product createProductFromImagePath(String imagePath) {
        String[] splitPath = imagePath.split("_");
        String category = splitPath[0];
        String subCategory = splitPath[1];

        ProductType productType = getProductType(subCategory);
        String productName = "Product for " + subCategory;
        String productInfo = "This is a " + productType.toString().toLowerCase() + " product for " + subCategory;
        String manufacturer = "Manufacturer for " + subCategory;
        int price = 100;
        boolean isDiscount = false;
        boolean isRecommend = true;

        // You might want to set imagePath to your product here if you have such field in your Product entity
        return new Product(productName, productType, price, productInfo, manufacturer, isDiscount, isRecommend);
    }

    // 단순히 DB 에 저장하는 로직
    private Product saveProduct(Product product) {
        log.info("Product details: " + product.toString());

        try {
            return productRepository.save(product);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException constraintException = (ConstraintViolationException) ex.getCause();
                log.error("Constraint violation for Product: " + product.toString(), constraintException.getSQLException());
            }
            throw ex;
        }
    }

    private ProductType getProductType(String subCategory) {
        return switch (subCategory) {
            case "blouse", "skirt", "dress", "two-piece", "short-padding", "boots" -> ProductType.WOMAN;
            case "hoodie", "knit-sweater", "long-shirts", "long-sleeve", "short-shirts", "short-sleeve", "sweatshirt",
                 "long", "shorts", "set-up", "cardigan", "coat", "jacket", "lightweight-padding", "long-padding",
                 "vest", "sandal", "sneakers", "bag", "cap", "socks" -> ProductType.UNISEX;
            default -> ProductType.MAN;
        };
    }

    private int computePriceFromImagePath(String imagePath) {
        return imagePath.hashCode() % 10000;
    }

    private boolean isDiscountFromImagePath(String imagePath) {
        return imagePath.hashCode() % 2 == 0;
    }

    private boolean isRecommendFromImagePath(String imagePath) {
        return imagePath.hashCode() % 3 == 0;
    }

    private String buildProductName(int index) {
        return String.format(PRODUCT_NAME_TEMPLATE, index);
    }

    private String buildProductInfo(ProductType productType) {
        return String.format(PRODUCT_INFO_TEMPLATE, productType.toString().toLowerCase());
    }

    private String buildManufacturerName(int index) {
        return String.format(MANUFACTURER_NAME_TEMPLATE, index);
    }

    private int computePrice(int index) {
        return 10000 * (index % 5 + 1);
    }

    private boolean isDiscount(int index) {
        return index % 2 == 0;
    }

    private boolean isRecommend(int index) {
        return index % 3 == 0;
    }

    private ProductType getProductType(int index) {
        if (index % 3 == 0) return ProductType.UNISEX;
        else if (index % 3 == 1) return ProductType.MAN;
        else return ProductType.WOMAN;
    }




}
