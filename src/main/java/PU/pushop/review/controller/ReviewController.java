package PU.pushop.review.controller;

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
     * @param request   reviewTitle, reviewContent, rating
     * @param paymentId
     * @return
     */
    @PostMapping("/new/{paymentId}")
    public ResponseEntity<ReviewDto> createReview(@RequestParam("images") List<MultipartFile> images,
                                                  @ModelAttribute ReviewCreateDto request,
                                                  @PathVariable Long paymentId) {

        ReviewDto createdReview = new ReviewDto(reviewService.createReview(request, images,paymentId));

        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    /**
     * 모든 리뷰 모아보기
     * @return
     */
    @GetMapping("/all")
    public List<ReviewDto> allReview() {
        return reviewService.allReview();
    }

    /**
     * 특정 상품의 리뷰 모아보기
     * @param productId
     * @return
     */
    @GetMapping("/product/{productId}")
    public List<ReviewDto> productReview(@Valid @PathVariable Long productId) {

        return reviewService.findReviewByProduct(productId);
    }

    /**
     * 특정 회원의 리뷰 모아보기
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
    public ResponseEntity<ReviewDto> updateReview(@Valid @PathVariable Long reviewId, @PathVariable Long memberId, @RequestBody ReviewCreateDto request) {

        ReviewDto updatedReivewDto = new ReviewDto(reviewService.updateReview(request, reviewId, memberId));

        return ResponseEntity.status(HttpStatus.OK).body(updatedReivewDto);

    }

    /**
     * 리뷰 삭제
     * @param reviewId
     * @return
     */
    @DeleteMapping("/{reviewId}/{memberId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId, @PathVariable Long memberId) {
        reviewService.deleteReview(reviewId, memberId);
        return ResponseEntity.ok(DELETE_SUCCESS);
    }

}
