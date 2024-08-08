package PU.pushop.global.dummydata.util;


import PU.pushop.InventoryProduct.entity.InventoryProduct;
import PU.pushop.InventoryProduct.repository.InventoryProductRepository;
import PU.pushop.category.entity.Category;
import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.product.entity.enums.ProductType;
import PU.pushop.product.repository.ProductColorRepository;
import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productManagement.entity.enums.Size;
import PU.pushop.productManagement.repository.ProductManagementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductManagementDataUtil {

    private final ProductManagementRepository productManagementRepository;
    private final ProductColorRepository productColorRepository;
    private final InventoryProductRepository inventoryProductRepository;
    private final Random random = new Random();

    /**
     * Generates ProductManagement entities for the given products, image paths, and colors.
     *
     * @param products    the list of Product objects
     * @param imagePaths  the list of image paths
     * @param colors      the list of colors
     */
    @Transactional
    public void generateProductManagementData(List<Product> products, List<String> imagePaths, List<ProductColor> colors, List<Category> categories) {
        // Get three random colors
        List<ProductColor> randomColors = getRandomThreeColors(colors);

        for (int i = 0; i < imagePaths.size(); i++) {
            // 상품 n 번 + 썸네일 n 번, 색상 3가지에 대해서 상품인벤토리 생성
            String imagePath = imagePaths.get(i);
            Product product = products.get(i);
            // 랜덤한 3가지 색상에 대해 ProductManagement 생성.
            for (ProductColor color : randomColors) {
                ProductManagement productManagement = createProductManagementV3(product, imagePath, color, categories);
                productManagementRepository.save(Objects.requireNonNull(productManagement));
            }
        }
    }
    private List<ProductColor> getRandomThreeColors(List<ProductColor> colors) {
        Collections.shuffle(colors);
        return colors.stream().limit(3).collect(Collectors.toList());
    }

    private ProductManagement createProductManagementV3(Product product, String imagePath, ProductColor color, List<Category> categories) {
        long initialStock = random.nextInt(1000);
        long additionalStock = 0;
        long productStock = initialStock;
        boolean isSoldOut = random.nextBoolean();
        boolean isRestockAvailable = random.nextBoolean();
        boolean isRestocked = random.nextBoolean();

        String[] splitPath = imagePath.split("_");
        String subCategory = splitPath[1];

        Category matchedCategory = getMatchedCategory(categories, subCategory);
        if (matchedCategory == null) return null;

        // Random size selection. 성별, 형용사, 하위카테고리 이름을 통해서, ERP 상의 상품 이름을 정의. 관리자/판매자 페이지에서 직접 상품 이름을 입력할 수 있다.
        Size size = Size.values()[random.nextInt(Size.values().length)];
        ProductType productType = product.getProductType();
        String adjective = getAdjectiveForSubCategory(subCategory);
        String erpProductName = String.format("%s %s %s", productType, adjective, subCategory);

        String productCode;
        while (true) {
            productCode = generateProductCode(matchedCategory, subCategory);
            if (!inventoryProductRepository.existsByProductCode(productCode)) {
                break;
            }
            log.warn("Duplicate product code detected: {}. Regenerating...", productCode);
        }

        InventoryProduct inventoryProduct = new InventoryProduct(product, color, matchedCategory, size, productCode, erpProductName, initialStock, productStock, isSoldOut, isRestockAvailable, isRestocked);

        inventoryProduct = inventoryProductRepository.save(inventoryProduct);

        return new ProductManagement(inventoryProduct, product, color, matchedCategory, size, initialStock, additionalStock, productStock, isSoldOut, isRestockAvailable, isRestocked);
    }

    // 상위 Bottom - 하위 long 카테고리에 대해서, B_01_A000001 과같이 상위_하위_대문자알파벳+6자리의숫자 형태로 상품코드를 저장한다. 숫자는 1개씩 추가된다.
    private String generateProductCode(Category parentCategory, String subCategory) {
        String parentCode = parentCategory.getName().substring(0, 1).toUpperCase();
        String subCode = getSubCategoryCode(subCategory);

        List<String> productCodes = inventoryProductRepository.findProductCodes(parentCode, subCode);

        char currentLetter = 'A';
        int maxSequenceNumber = 0;

        if (!productCodes.isEmpty()) {
            for (String code : productCodes) {
                if (code.length() == 12) {
                    String letterPart = code.substring(6, 7);
                    String sequencePart = code.substring(7);

                    try {
                        int sequenceNumber = Integer.parseInt(sequencePart);
                        if (sequenceNumber > maxSequenceNumber) {
                            maxSequenceNumber = sequenceNumber;
                            currentLetter = letterPart.charAt(0);
                        }
                    } catch (NumberFormatException e) {
                        log.error("Invalid sequence part in product code: {}", code, e);
                    }
                }
            }
            if (maxSequenceNumber >= 999999) {
                currentLetter++;
                maxSequenceNumber = 0;
            }
        }
        int nextSequenceNumber = maxSequenceNumber + 1;
        String sequenceFormatted = String.format("%06d", nextSequenceNumber);
        String productCode = String.format("%s_%s_%c%s", parentCode, subCode, currentLetter, sequenceFormatted);

        return productCode;
    }

    private static @Nullable Category getMatchedCategory(List<Category> categories, String subCategory) {
        // Subcategory matching using the categories list
        Category matchedCategory = null;
        for (Category cat : categories) {
            if (cat.getName().equals(subCategory)) {
                matchedCategory = cat;
                break;
            }
        }
        // If no matching category found, return null or throw an exception
        if (matchedCategory == null) {
            return null;
        }
        return matchedCategory;
    }

    public ProductColor getRandomColor() {
        List<ProductColor> colors = productColorRepository.findAll();
        int randomIndex = new Random().nextInt(colors.size());
        return colors.get(randomIndex);
    }

    // 하위 카테고리이름에 맞춰서, 형용사(Adjective)를 정해준다. 더미데이터에만 반영된다.
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

    // 하위 카테고리의 코드를 정해준다.
    private String getSubCategoryCode(String subCategory) {
        return switch (subCategory) {
            case "blouse" -> "01";
            case "hoodie" -> "02";
            case "knit-sweater" -> "03";
            case "long-shirts" -> "04";
            case "long-sleeve" -> "05";
            case "short-shirts" -> "06";
            case "short-sleeve" -> "07";
            case "sweatshirt" -> "08";
            case "long" -> "01";
            case "shorts" -> "02";
            case "skirt" -> "03";
            case "dress" -> "01";
            case "set-up" -> "02";
            case "two-piece" -> "03";
            case "cardigan" -> "01";
            case "coat" -> "02";
            case "jacket" -> "03";
            case "lightweight-padding" -> "04";
            case "long-padding" -> "05";
            case "mustang" -> "06";
            case "short-padding" -> "07";
            case "vest" -> "08";
            case "boots" -> "01";
            case "sandal" -> "02";
            case "sneakers" -> "03";
            case "bag" -> "01";
            case "cap" -> "02";
            case "socks" -> "03";
            default -> "00";
        };
    }

    /**
     * Creates a new instance of ProductManagement with random values for fields.
     *
     * @param index the index for creating the ProductManagement instance
     * @return a new instance of ProductManagement
     */
    private ProductManagement createProductManagementV1(int index) {
        long additionalStock = random.nextInt(500);
        boolean isRestockAvailable = random.nextBoolean();
        boolean isRestocked = random.nextBoolean();
        boolean isSoldOut = random.nextBoolean();
        long initialStock = random.nextInt(1000);
        long productStock = random.nextInt(1000);

        // XSMALL("X-Small"), SMALL("Small"), MEDIUM("Medium"), LARGE("Large"), XLARGE("X-Large"), FREE("Free")
        // 랜덤하게 입력.
        Size size = Size.values()[random.nextInt(Size.values().length)];

        long category_id = random.nextInt(34) + 1; // generate random category id between 1 and 34
        long product_id = random.nextInt(500) + 1; // generate random product id between 1 and 500
        // category_id 만을 가지고 있는 Category
        Category categoryById = Category.createCategoryById(category_id);
        // product_id 만을 가지고 있는 Product
        Product productById = Product.createProductById(product_id);
        // product color 를 1번부터 14번까지 색상중 랜덤하게 들고옵니다.
        ProductColor color = getRandomColor();

        return new ProductManagement(initialStock, additionalStock, categoryById, productById, productStock, size, color, isRestockAvailable, isRestocked, isSoldOut);
    }

    private ProductManagement createProductManagementV2(Product product, String imagePath, ProductColor color, List<Category> categories) {
        long additionalStock = 0;
        boolean isRestockAvailable = true;
        boolean isRestocked = false;
        boolean isSoldOut = false;
        long initialStock = random.nextInt(1000);
        long productStock = initialStock;

        String[] splitPath = imagePath.split("_");
        String category = splitPath[0];
        String subCategory = splitPath[1];

        // Subcategory matching using the categories list
        Category matchedCategory = getMatchedCategory(categories, subCategory);
        if (matchedCategory == null) return null; // or throw exception

        // Random size selection
        Size size = Size.values()[random.nextInt(Size.values().length)];

        // Preset color selection
//        ProductColor productColor = new ProductColor(color);

        return new ProductManagement(initialStock, additionalStock, matchedCategory, product, productStock, size, color, isRestockAvailable, isRestocked, isSoldOut);
    }
}
