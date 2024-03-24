package PU.pushop.product.controller;

import PU.pushop.product.service.ProductThumbnailServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/thumbnail")
@RequiredArgsConstructor
public class ProductThumbnailControllerV1 {
    private final ProductThumbnailServiceV1 productThumbnailService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadThumbnail(@RequestParam("productId") Long productId, @RequestParam("image") MultipartFile image) {
        productThumbnailService.uploadThumbnail(productId, image);
        return ResponseEntity.status(HttpStatus.CREATED).body("썸네일 업로드 완료");
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteThumbnail(@PathVariable Long productId) {
        productThumbnailService.deleteThumbnail(productId);
        return ResponseEntity.status(HttpStatus.OK).body("썸네일 삭제 완료");
    }
}
