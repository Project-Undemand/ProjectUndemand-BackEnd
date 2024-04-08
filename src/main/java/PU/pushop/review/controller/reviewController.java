package PU.pushop.review.controller;

import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.payment.repository.PaymentRepository;
import PU.pushop.review.entity.Review;
import PU.pushop.review.model.ReviewDto;
import PU.pushop.review.service.reviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1/review")
@RequiredArgsConstructor
@Slf4j
public class reviewController {

    private final reviewService reviewService;

    /**
     * 리뷰 작성
     *
     * @param request   reviewTitle, reviewContent, rating
     * @param paymentId
     * @return
     */
    @PostMapping("/new/{paymentId}")
    public ResponseEntity<?> createReview(@RequestBody ReviewDto request, @PathVariable Long paymentId) {

        Review review = ReviewDto.requestForm(request);

        ReviewDto createdReview = new ReviewDto(reviewService.createReview(review, paymentId));

        return ResponseEntity.ok(createdReview);
    }

    /**
     * 특정 상품의 리뷰 모아보기
     *
     * @param productId
     * @return
     */
    @GetMapping("/product/{productId}")
    public List<ReviewDto> productReview(@Valid @PathVariable Long productId) {

        return reviewService.findReviewByProduct(productId);
    }

    /**
     * 특정 회원의 리뷰 모아보기
     *
     * @param memberId
     * @return
     */
    @GetMapping("/user/{memberId}")
    public List<ReviewDto> userReview(@Valid @PathVariable Long memberId) {

        return reviewService.findReviewByUser(memberId);
    }

    /**
     * 리뷰 상세보기
     * @param reviewId
     * @return
     */
    @GetMapping("/{reviewId}")
    public ReviewDto reviewDatail(@Valid @PathVariable Long reviewId) {
        return reviewService.reviewDetail(reviewId);
    }

    /**
     * 리뷰 수정
     * @param reviewId
     * @param request
     * @return
     */
    @PutMapping("/{reviewId}/{memberId}")
    public ResponseEntity<?> updateReview(@Valid @PathVariable Long reviewId, @PathVariable Long memberId, @RequestBody ReviewDto request) {

        Review updatedreview = ReviewDto.requestForm(request);
        ReviewDto updatedReivewDto = new ReviewDto(reviewService.updateReview(updatedreview, reviewId, memberId));

        return ResponseEntity.ok(updatedReivewDto);

    }

    /**
     * 리뷰 삭제
     * @param reviewId
     * @return
     */
    @DeleteMapping("/{reviewId}/{memberId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, @PathVariable Long memberId) {
        reviewService.deleteReview(reviewId, memberId);
        return ResponseEntity.ok("리뷰 삭제 완료 " + reviewId);
    }

}
