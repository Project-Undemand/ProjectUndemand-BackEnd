package PU.pushop.global.dummydata.util;

import PU.pushop.product.entity.Product;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.productThumbnail.entity.ProductThumbnail;
import PU.pushop.productThumbnail.repository.ProductThumbnailRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.stream.IntStream;


/**
 * Utility class for generating and saving product thumbnail data.
 */
@Component
@RequiredArgsConstructor
public class ProductThumbnailDataUtil {

    private final ProductRepositoryV1 productRepository;
    private final ProductThumbnailRepositoryV1 productThumbnailRepository;

    public void generateProductThumbnailData(int count) {
        IntStream.rangeClosed(1, count).forEach(i -> {
            // 이미지 경로 생성
            String imagePath = String.format("/images/products/product%d.png", i % 5 + 1);

            // 상품 조회
            Product product = productRepository.findById((long) i)
                    .orElseThrow(() -> new NoSuchElementException("해당 상품이 존재하지 않습니다."));

            // 썸네일 생성 및 저장
            ProductThumbnail productThumbnail = new ProductThumbnail(product, imagePath);
            productThumbnailRepository.save(productThumbnail);
        });
    }
}
