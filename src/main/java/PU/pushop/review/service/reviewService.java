package PU.pushop.review.service;


import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.payment.repository.PaymentRepository;
import PU.pushop.review.entity.Review;
import PU.pushop.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class reviewService {
    public final PaymentRepository paymentRepository;
    public final ReviewRepository reviewRepository;

    /**
     * 리뷰 작성
     * @param review reviewTitle, reviewContent, rating
     * @param paymentId
     * @return
     */
    public Review createReview(Review review, Long paymentId) {
        PaymentHistory paymentHistory = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoSuchElementException("결제 내역을 찾을 수 없습니다."));

        review.setPaymentHistory(paymentHistory);
        reviewRepository.save(review);
        return review;
    }

    public void deleteReview(Long reviewId) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("글을 찾을 수 없습니다."));

        reviewRepository.delete(existingReview);
    }
}
