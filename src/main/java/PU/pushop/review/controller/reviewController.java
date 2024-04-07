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
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewDto request, @PathVariable Long paymentId) {

        Review review = ReviewDto.requestForm(request);
        System.out.println("Received request body: " + review.getRating());

        Review createdReview = reviewService.createReview(review, paymentId);

        return ResponseEntity.ok(createdReview);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok("리뷰 삭제 완료 " + reviewId);
    }
}
