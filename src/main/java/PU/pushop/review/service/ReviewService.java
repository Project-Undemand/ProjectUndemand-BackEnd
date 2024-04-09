package PU.pushop.review.service;


import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.payment.repository.PaymentRepository;
import PU.pushop.product.entity.Product;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.review.entity.Review;
import PU.pushop.review.model.ReviewDto;
import PU.pushop.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ReviewService {
    public final PaymentRepository paymentRepository;
    public final ReviewRepository reviewRepository;
    public final ProductRepositoryV1 productRepository;
    public final MemberRepositoryV1 memberRepository;

    /**
     * 리뷰 작성
     * @param review reviewTitle, reviewContent, rating
     * @param paymentId
     * @return
     */
    public Review createReview(Review review, Long paymentId) {
        PaymentHistory paymentHistory = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoSuchElementException("해당 주문 내역을 찾을 수 없습니다."));

        // 이미 Review가 존재하는지 확인
        Optional<Review> existingReview = reviewRepository.findByPaymentHistory(paymentHistory);
        if (existingReview.isPresent()) {
            throw new IllegalStateException("이미 후기가 작성되었습니다.");
        }
        review.setPaymentHistory(paymentHistory);
        reviewRepository.save(review);

        paymentHistory.setReview(true);

        return review;
    }

    /**
     * 모든 리뷰 보기
     * @return
     */
    public List<ReviewDto> allReview() {
        List<Review> reviews = reviewRepository.findAll();
        if (reviews.isEmpty()) {
            throw new IllegalStateException("리뷰가 없습니다.");
        }
        return reviews.stream().map(ReviewDto::new).collect(Collectors.toList());
    }

    /**
     * 특정 상품의 리뷰 모아보기
     * @param productId
     * @return
     */
    public List<ReviewDto> findReviewByProduct(Long productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));

        List<Review> reviews = reviewRepository.findByPaymentHistoryProduct(product);

        if (reviews.isEmpty()) {
            throw new IllegalStateException("리뷰가 없습니다.");
        }

        return reviews.stream().map(ReviewDto::new).collect(Collectors.toList());
    }

    /**
     * 특정 회원의 리뷰 모아보기
     * @param memberId
     * @return
     */
    public List<ReviewDto> findReviewByUser(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));

        List<Review> reviews = reviewRepository.findByPaymentHistoryMember(member);

        if (reviews.isEmpty()) {
            throw new IllegalStateException("리뷰가 없습니다.");
        }

        return reviews.stream().map(ReviewDto::new).collect(Collectors.toList());
    }

    /**
     * 리뷰 상세보기
     * @param reviewId
     * @return
     */
    public ReviewDto reviewDetail(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("해당 후기를 찾을 수 없습니다."));

        ReviewDto reviewDetail = new ReviewDto(review);

        return reviewDetail;
    }

    /**
     * 리뷰 수정
     * @param updatedReview
     * @param reviewId
     * @return
     */
    public Review updateReview(Review updatedReview, Long reviewId, Long memberId) {

        Review currentReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("해당 후기를 찾을 수 없습니다."));

        Long reviewWriterId = currentReview.getPaymentHistory().getMember().getId();

        if (!memberId.equals(reviewWriterId)) {
            throw new SecurityException("접근 권한이 없습니다.");
        }

        currentReview.setReviewContent(updatedReview.getReviewContent());
        currentReview.setRating(updatedReview.getRating());

        return reviewRepository.save(currentReview);
    }

    /**
     * 리뷰 삭제
     * @param reviewId
     */
    public void deleteReview(Long reviewId, Long memberId) {
        Review currentReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("글을 찾을 수 없습니다."));
        Long reviewWriterId = currentReview.getPaymentHistory().getMember().getId();

        if (!memberId.equals(reviewWriterId)) {
            throw new SecurityException("접근 권한이 없습니다.");
        }

        reviewRepository.delete(currentReview);
    }

}
