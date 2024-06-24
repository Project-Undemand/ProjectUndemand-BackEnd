package PU.pushop.review.controller;

import PU.pushop.review.entity.Review;
import PU.pushop.review.model.ReviewCreateDto;
import PU.pushop.review.model.ReviewDto;
import PU.pushop.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static PU.pushop.global.ResponseMessageConstants.DELETE_SUCCESS;

@RestController
@RequestMapping("api/v1/review")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 작성
     * @param images 5개 이하의 리뷰 이미지
     * @param reviewContent 리뷰 내용
     * @param rating 별점 1개 ~ 5개
     * @param paymentId 리뷰 작성할 상품
     * @return 작성 완료 메시지
     */
    @PostMapping("/new/{paymentId}")
    public ResponseEntity<String> createReview(
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "rating") String rating,
            @RequestPart(value = "reviewContent") String reviewContent,
            @PathVariable Long paymentId) {

        ReviewCreateDto request = new ReviewCreateDto();
        request.setReviewContent(reviewContent);
        request.setRating(Integer.parseInt(rating));

        Review createdReview = reviewService.createReview(request, images, paymentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview.getReviewId() + " 번 , 구매후기가 작성 되었습니다. ");
    }

    /**
     * 모든 리뷰 모아보기
     * @return review list
     */
    @GetMapping("/all")
    public List<ReviewDto> allReview() {
        return reviewService.allReview();
    }

    /**
     * 특정 상품의 리뷰 모아보기
     * @param productId productId
     * @return review List
     */
    @GetMapping("/product/{productId}")
    public List<ReviewDto> productReview(@Valid @PathVariable Long productId) {

        return reviewService.findReviewByProduct(productId);
    }

    /**
     * 특정 회원의 리뷰 모아보기
     * @param memberId 작성자
     * @return review list
     */
    @GetMapping("/user/{memberId}")
    public List<ReviewDto> userReview(@Valid @PathVariable Long memberId) {

        return reviewService.findReviewByUser(memberId);
    }

    /**
     * 리뷰 상세보기
     * @param reviewId reviewId
     * @return review detail
     */
    @GetMapping("/{reviewId}")
    public ReviewDto reviewDatail(@Valid @PathVariable Long reviewId) {
        return reviewService.reviewDetail(reviewId);
    }

    /**
     * 리뷰 수정
     * @param reviewId review id
     * @param request review detail
     * @return review detail
     */
    @PutMapping("/{reviewId}/{memberId}")
    public ResponseEntity<ReviewDto> updateReview(@Valid @PathVariable Long reviewId, @PathVariable Long memberId, @RequestBody ReviewCreateDto request) {

        ReviewDto updatedReivewDto = new ReviewDto(reviewService.updateReview(request, reviewId, memberId));

        return ResponseEntity.status(HttpStatus.OK).body(updatedReivewDto);

    }

    /**
     * 리뷰 삭제
     * @param reviewId review id
     * @return none
     */
    @DeleteMapping("/{reviewId}/{memberId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId, @PathVariable Long memberId) {
        reviewService.deleteReview(reviewId, memberId);
        return ResponseEntity.ok(DELETE_SUCCESS);
    }

}
