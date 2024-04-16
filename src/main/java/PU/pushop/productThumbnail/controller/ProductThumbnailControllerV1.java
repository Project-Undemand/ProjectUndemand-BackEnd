package PU.pushop.productThumbnail.controller;

import PU.pushop.product.entity.Product;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.productThumbnail.entity.ProductThumbnail;
import PU.pushop.productThumbnail.service.ProductThumbnailServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static PU.pushop.global.ResponseMessageConstants.DELETE_SUCCESS;
import static PU.pushop.global.ResponseMessageConstants.PRODUCT_NOT_FOUND;

@RestController
@RequestMapping("/api/v1/thumbnail")
@RequiredArgsConstructor
public class ProductThumbnailControllerV1 {
    private final ProductThumbnailServiceV1 productThumbnailService;
    private final ProductRepositoryV1 productRepository;

    // 썸네일 업로드
    @PostMapping("/upload")
    public ResponseEntity<String> uploadThumbnail(@RequestParam("productId") Long productId, @RequestParam("image") List<MultipartFile> images) {
        Product product = productRepository.findByProductId(productId).orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));
        productThumbnailService.uploadThumbnail(product, images);
        return ResponseEntity.status(HttpStatus.CREATED).body("썸네일 업로드 완료");
    }

    // 썸네일 삭제
    @DeleteMapping("/delete/{thumbnailId}")
    public ResponseEntity<String> deleteThumbnail(@PathVariable Long thumbnailId) {
        productThumbnailService.deleteThumbnail(thumbnailId);
        return ResponseEntity.status(HttpStatus.OK).body(DELETE_SUCCESS);
    }
    
    // 상품 id로 썸네일 조회 (경로 리스트)
    @GetMapping("/{productId}")
    public ResponseEntity<List<String>> getProductThumbnails(@PathVariable Long productId) {
        List<ProductThumbnail> thumbnails = productThumbnailService.getProductThumbnails(productId);
        if (!thumbnails.isEmpty()) {
            List<String> thumbnailPaths = thumbnails.stream()
                    .map(ProductThumbnail::getImagePath)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(thumbnailPaths);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
