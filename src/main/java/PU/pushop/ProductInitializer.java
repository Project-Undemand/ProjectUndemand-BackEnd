package PU.pushop;

import PU.pushop.category.entity.Category;
import PU.pushop.global.dummydata.util.*;
import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.product.repository.ProductRepositoryV1;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("develop")
@RequiredArgsConstructor
public class ProductInitializer implements ApplicationRunner {

    private final ProductRepositoryV1 productRepositoryV1;
    private final Hibernate6Module hibernate6Module;
    // 더미데이터 생성 및 DB 저장역할을 하는 Util Class
    private final ProductDataUtil productDataUtil;
    private final ProductThumbnailDataUtil thumbnailDataUtil;
    private final CategoryDataUtil categoryDataUtil;
    private final ProductColorDataUtil productColorDataUtil;
    private final ProductManagementDataUtil productManagementDataUtil;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // 상품 더미 데이터 생성후, 더미 데이터 DB 저장
        List<Product> products = productDataUtil.generateProductDataWithImages(imagePaths);

        // 상품 썸네일 더미 데이터 생성후, 더미 데이터 DB 저장
        thumbnailDataUtil.generateProductThumbnailDataV3(products, imagePaths);

        // 카테고리 데이터 (34개)
        List<Category> categories = categoryDataUtil.generateCategoryData();

        // 상품 색상 더미데이터 생성후, 더미 데이터 DB 저장
        List<ProductColor> productColors = productColorDataUtil.generateAndSaveProductColorData();

        // 상품 관리 더미데이터 생성후, 더미 데이터 DB 저장
        productManagementDataUtil.generateProductManagementData(products, imagePaths, productColors, categories);
    }


    List<String> imagePaths = Arrays.asList(
            "accessories_bag_1.jpg", "outer_cardigan_2.jpg", "shoes_sneakers_1.jpg",
            "accessories_bag_2.jpg", "outer_coat_1.jpg", "shoes_sneakers_2.jpg",
            "accessories_cap_1.jpg", "outer_coat_2.jpg", "top_blouse_1.jpg",
            "accessories_cap_2.jpg", "outer_jacket_1.jpg", "top_blouse_2.jpg",
            "accessories_socks_1.jpg", "outer_jacket_2.jpg", "top_hoodie_1.jpg",
            "accessories_socks_2.jpg", "outer_lightweight-padding_1.jpg","top_hoodie_2.jpg",
            "bottom_long_1.jpg", "outer_lightweight-padding_2.jpg", "top_knit-sweater_1.jpg",
            "bottom_long_2.jpg", "outer_long-padding_1.jpg", "top_knit-sweater_2.jpg",
            "bottom_shorts_1.jpg", "outer_long-padding_2.jpg", "top_long-shirts_1.jpg",
            "bottom_shorts_2.jpg", "outer_mustang_1.jpg", "top_long-shirts_2.jpg",
            "bottom_skirt_1.jpg", "outer_mustang_2.jpg", "top_long-sleeve_1.jpg",
            "bottom_skirt_2.jpg", "outer_short-padding_1.jpg", "top_long-sleeve_2.jpg",
            "dress&set_dress_1.jpg", "outer_short-padding_2.jpg", "top_short-shirts_1.jpg",
            "dress&set_dress_2.jpg", "outer_vest_1.jpg", "top_short-shirts_2.jpg",
            "dress&set_set-up_1.jpg", "outer_vest_2.jpg", "top_short-sleeve_1.jpg",
            "dress&set_set-up_2.jpg", "shoes_boots_1.jpg", "top_short-sleeve_2.jpg",
            "dress&set_two-piece_1.jpg", "shoes_boots_2.jpg", "top_sweatshirt_1.jpg",
            "dress&set_two-piece_2.jpg", "shoes_sandal_1.jpg", "top_sweatshirt_2.jpg",
            "outer_cardigan_1.jpg", "shoes_sandal_2.jpg"
    );
}
