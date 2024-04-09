package PU.pushop.reviewImg;

import PU.pushop.review.entity.Review;
import PU.pushop.review.repository.ReviewRepository;
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
@Transactional
@RequiredArgsConstructor
public class ReviewImgService {
    private final ReviewImgRepository reviewImgRepository;
    private final ReviewRepository reviewRepository;


    public void uploadReviewImg(Long reviewId, List<MultipartFile> images) {

        try {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글을 찾을 수 없습니다."));

        String uploadsDir = "src/main/resources/static/uploads/thumbnails/";

        // 각 이미지 파일에 대해 업로드 및 DB 저장 수행
        for (MultipartFile image : images) {
            String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + image.getOriginalFilename();
            String filePath = uploadsDir + fileName;
            String dbFilePath = "/uploads/thumbnails/" + fileName;

            saveImage(image, filePath);

            ReviewImg reviewImg = new ReviewImg(review, filePath);
            reviewImg.setReview(review);
            reviewImg.setReviewImgPath(dbFilePath);
            reviewImgRepository.save(reviewImg);
        }

        } catch (IOException e) {
            // 파일 저장 중 오류가 발생한 경우 처리
            e.printStackTrace();
        }

    }

    public void deleteReviewImg(Long reviewImgId) {
        ReviewImg reviewImg = reviewImgRepository.findById(reviewImgId)
                .orElseThrow(() -> new RuntimeException("해당 사진을 찾을 수 없습니다."));

        String imagePath = reviewImg.getReviewImgPath();

        reviewImgRepository.delete(reviewImg);

        deleteImageFile(imagePath);

    }

    public List<ReviewImg> getReviewImg(Long reviewId) {
        return reviewImgRepository.findByReview_ReviewId(reviewId);
    }

    private void saveImage(MultipartFile image, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.write(path, image.getBytes());
    }

    private void deleteImageFile(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // 파일 삭제 중 오류 발생 시 예외 처리
            e.printStackTrace();
        }
    }

}
