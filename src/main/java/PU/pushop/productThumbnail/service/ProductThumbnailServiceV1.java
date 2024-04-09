package PU.pushop.productThumbnail.service;

import PU.pushop.product.entity.Product;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.product.service.ProductServiceV1;
import PU.pushop.productThumbnail.entity.ProductThumbnail;
import PU.pushop.productThumbnail.repository.ProductThumbnailRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ProductThumbnailServiceV1 {
    private final ProductThumbnailRepositoryV1 productThumbnailRepository;
    private final ProductRepositoryV1 productRepository;

    /**
     * 썸네일 등록
     * @param productId
     * @param images
     */
    public void uploadThumbnail(Long productId, List<MultipartFile> images) {
        try {
            // Product 엔티티 가져오기
//            Product product = productService.findProductById(productId);

            Product product = productRepository.findByProductId(productId)
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
            // 이미지 파일 저장을 위한 경로 설정
            String uploadsDir = "src/main/resources/static/uploads/thumbnails/";

            // 각 이미지 파일에 대해 업로드 및 DB 저장 수행
            for (MultipartFile image : images) {
                String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + image.getOriginalFilename();
                String filePath = uploadsDir + fileName;
                String dbFilePath = "/uploads/thumbnails/" + fileName;

                // 저장된 이미지 파일 경로를 DB에 저장
                saveImage(image, filePath);

                // ProductThumbnail 엔티티 생성 및 저장
                ProductThumbnail thumbnail = new ProductThumbnail(product, filePath);
                thumbnail.setProduct(product);
//                thumbnail.setImagePath(filePath);
                thumbnail.setImagePath(dbFilePath);
                productThumbnailRepository.save(thumbnail);
            }
        } catch (IOException e) {
            // 파일 저장 중 오류가 발생한 경우 처리
            e.printStackTrace();
        }
    }
    // 이미지 파일을 저장하는 메서드
    private void saveImage(MultipartFile image, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.write(path, image.getBytes());
    }

    /**
     * 썸네일 삭제
     * @param thumbnailId
     */
    public void deleteThumbnail(Long thumbnailId) {
        // 썸네일 엔티티 조회
        ProductThumbnail thumbnail = productThumbnailRepository.findById(thumbnailId)
                .orElseThrow(() -> new RuntimeException("해당 사진을 찾을 수 없습니다."));

        // 썸네일 파일 경로 가져오기
        String imagePath = thumbnail.getImagePath();

        // 썸네일 데이터베이스에서 삭제
        productThumbnailRepository.delete(thumbnail);

        // 파일 삭제
        deleteImageFile(imagePath);
    }
    // 파일 삭제 메서드
    private void deleteImageFile(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // 파일 삭제 중 오류 발생 시 예외 처리
            e.printStackTrace();
        }
    }

    //썸네일 조회
    public List<ProductThumbnail> getProductThumbnails(Long productId) {
        return productThumbnailRepository.findByProduct_ProductId(productId);
    }
}
