package PU.pushop.contentImgs.controller;

import PU.pushop.contentImgs.service.ContentImgService;
import PU.pushop.contentImgs.entity.ContentImages;
import PU.pushop.product.entity.Product;
import PU.pushop.product.repository.ProductRepositoryV1;
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
@RequestMapping("/api/v1/product/image")
@RequiredArgsConstructor
public class ContentImgController {

    private final ContentImgService contentImgService;
    private final ProductRepositoryV1 productRepository;

    // 이미지 업로드
    @PostMapping("/upload")
    public ResponseEntity<String> uploadContentImg(@RequestParam("productId") Long productId, @RequestParam("image") List<MultipartFile> images) {
        Product product = productRepository.findByProductId(productId).orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));
        contentImgService.uploadContentImage(product, images);
        return ResponseEntity.status(HttpStatus.CREATED).body("이미지 업로드 완료");
    }

    // 이미지 삭제
    @DeleteMapping("/delete/{contentImgId}")
    public ResponseEntity<String> deleteContentImg(@PathVariable Long contentImgId) {
        contentImgService.deleteContentImage(contentImgId);
        return ResponseEntity.status(HttpStatus.OK).body(DELETE_SUCCESS);
    }

    // 상품 id로 이미지 조회 (경로 리스트)
    @GetMapping("/{productId}")
    public ResponseEntity<List<String>> getProductContentImgs(@PathVariable Long productId) {
        List<ContentImages> contentImages = contentImgService.getContentImgs(productId);
        if (!contentImages.isEmpty()) {
            List<String> imagePaths = contentImages.stream()
                    .map(ContentImages::getImagePath)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(imagePaths);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
