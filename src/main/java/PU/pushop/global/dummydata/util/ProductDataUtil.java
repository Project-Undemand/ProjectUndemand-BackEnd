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
import java.util.Random;
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

        Random rand = new Random();

        // 제조업체 . 실제 존재하지 않습니다.
        String[] manufacturers = {
                "FashionThreads Inc",
                "Elite Apparel Co",
                "Stylish Stitches Ltd",
                "Urban Garments Manufacturing",
                "ChicWear Productions",
                "TrendSet Clothing Co",
                "Elegant Fabrics Ltd"
        };
        // 가격을 100, 200, 300, 400, 500 중에서 랜덤하게 select.
        int[] prices = {200, 300, 400};
        int[] discountRates = {10, 20, 30, 40, 50};

        int price = prices[rand.nextInt(prices.length)];
        String manufacturer = manufacturers[rand.nextInt(manufacturers.length)];
        ProductType productType = getProductType(subCategory);

        // isDiscount 여부에 따라, discountRate 를 정해주도록 [24.06.03]
        boolean isDiscount = rand.nextBoolean(); // 할인 여부
        Integer discountRate;
        if (isDiscount) {
            discountRate = discountRates[rand.nextInt(discountRates.length)];
        } else {
            discountRate = null;
        }
        boolean isRecommend = rand.nextBoolean(); // 추천 상품 여부

        String productName = buildProductName(manufacturer, productType, subCategory, discountRate);
        String productInfo = "This is a " + productType.toString().toLowerCase() + " product for " + subCategory;

        // You might want to set imagePath to your product here if you have such field in your Product entity
        return new Product(productName, productType, price, productInfo, manufacturer, isDiscount, discountRate, isRecommend);
    }

    private ProductType getProductType(String subCategory) {
        return switch (subCategory) {
            case "blouse", "skirt", "dress", "two-piece", "short-padding", "boots" -> ProductType.WOMAN;
            case "hoodie", "knit-sweater", "long-shirts", "long-sleeve", "short-shirts",
                  "shorts", "cardigan", "jacket", "lightweight-padding", "long-padding",
                 "vest", "sandal", "sneakers", "bag", "cap", "socks" -> ProductType.UNISEX;
            default -> ProductType.MAN;
        };
    }

    private String buildProductName(String manufacturer, ProductType productType, String subCategory, Integer discountRate) {
        String adjective = getAdjectiveForSubCategory(subCategory);
        String discountText = (discountRate != null) ? " [" + discountRate + "% 할인특가]" : "";
        return String.format("[%s] %s %s %s%s", manufacturer, productType, adjective, subCategory, discountText);
    }

    private String getAdjectiveForSubCategory(String subCategory) {
        return switch (subCategory) {
            case "blouse" -> "Elegant";
            case "skirt" -> "Chic";
            case "dress" -> "Graceful";
            case "two-piece" -> "Sophisticated";
            case "short-padding" -> "Warm";
            case "long-padding" -> "Full-body warmth";
            case "lightweight-padding" -> "Light";
            case "boots" -> "Sturdy";
            case "sandal" -> "Breezy";
            case "sneakers" -> "Sporty";
            case "hoodie" -> "Casual";
            case "knit-sweater" -> "Snug";
            case "sweatshirt" -> "Relaxed";
            case "long-shirts" -> "Formal";
            case "short-shirts" -> "Cool";
            case "long-sleeve" -> "Comfy";
            case "short-sleeve" -> "Breathable";
            case "shorts" -> "Athletic";
            case "long" -> "Flowy";
            case "set-up" -> "Coordinated";
            case "cardigan" -> "Layered";
            case "coat" -> "Classic";
            case "jacket" -> "Stylish";
            case "vest" -> "Versatile";
            case "cap" -> "Trendy";
            case "socks" -> "Cozy";
            case "bag" -> "Fashionable";
            default -> "Unique";
        };
    }


    // 단순히 DB 에 저장하는 로직
    private Product saveProduct(Product product) {
        try {
            return productRepository.save(product);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause() instanceof ConstraintViolationException constraintException) {
                log.error("Constraint violation for Product: " + product.toString(), constraintException.getSQLException());
            }
            throw ex;
        }
    }

    private String buildProductInfo(ProductType productType) {
        return String.format(PRODUCT_INFO_TEMPLATE, productType.toString().toLowerCase());
    }

    private String buildManufacturerName(int index) {
        return String.format(MANUFACTURER_NAME_TEMPLATE, index);
    }

}
