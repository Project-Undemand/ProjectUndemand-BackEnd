package PU.pushop;

import PU.pushop.global.dummydata.util.*;
import PU.pushop.product.repository.ProductRepositoryV1;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

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
        productDataUtil.generateProductData(500);

        // 상품 썸네일 더미 데이터 생성후, 더미 데이터 DB 저장
        thumbnailDataUtil.generateProductThumbnailData(500);

        // 카테고리 데이터 (34개)
        categoryDataUtil.generateCategoryData();

        // 상품 색상 더미데이터 생성후, 더미 데이터 DB 저장
        productColorDataUtil.generateAndSaveProductColorData();

        // 상품 관리 더미데이터 생성후, 더미 데이터 DB 저장
        productManagementDataUtil.generateProductManagementData(500);
    }
}
