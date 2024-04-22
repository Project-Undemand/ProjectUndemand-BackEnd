package PU.pushop.global.dummydata.util;


import PU.pushop.category.entity.Category;
import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.product.repository.ProductColorRepository;
import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productManagement.entity.enums.Size;
import PU.pushop.productManagement.repository.ProductManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class ProductManagementDataUtil {

    private final ProductManagementRepository productManagementRepository;
    private final ProductColorRepository productColorRepository;
    private final Random random = new Random();


    /**
     * Generates and saves a specified number of ProductManagement records.
     *
     * @param count the number of ProductManagement records to generate and save
     */
    public void generateProductManagementData(int count) {
        List<ProductManagement> productManagementList = IntStream.rangeClosed(1, count)
                .mapToObj(this::createProductManagement)
                .collect(Collectors.toList());

        productManagementRepository.saveAll(productManagementList);
    }

    /**
     * Creates a new instance of ProductManagement with random values for fields.
     *
     * @param index the index for creating the ProductManagement instance
     * @return a new instance of ProductManagement
     */
    private ProductManagement createProductManagement(int index) {
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

    public ProductColor getRandomColor() {
        List<ProductColor> colors = productColorRepository.findAll();
        int randomIndex = new Random().nextInt(colors.size());
        return colors.get(randomIndex);
    }
}
