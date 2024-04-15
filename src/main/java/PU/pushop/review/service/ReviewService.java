package PU.pushop.review.service;


import PU.pushop.global.authorization.MemberAuthorizationUtil;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.payment.repository.PaymentRepository;
import PU.pushop.product.entity.Product;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.review.entity.Review;
import PU.pushop.review.model.ReviewCreateDto;
import PU.pushop.review.model.ReviewDto;
import PU.pushop.review.repository.ReviewRepository;
import PU.pushop.reviewImg.ReviewImg;
import PU.pushop.reviewImg.ReviewImgRepository;
import PU.pushop.reviewImg.ReviewImgService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static PU.pushop.global.ResponseMessageConstants.*;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ReviewService {
    public final PaymentRepository paymentRepository;
    public final ReviewRepository reviewRepository;
    public final ProductRepositoryV1 productRepository;
    public final MemberRepositoryV1 memberRepository;
    public final ModelMapper modelMapper;
    public final ReviewImgService reviewImgService;
    public final ReviewImgRepository reviewImgRepository;

    /**
     * 리뷰 작성
     * @param request reviewTitle, reviewContent, rating
     * @param paymentId
     * @return
     */
    public Review createReview(ReviewCreateDto request, List<MultipartFile> images, Long paymentId) {
        PaymentHistory paymentHistory = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoSuchElementException("해당 주문 내역을 찾을 수 없습니다."));

        // 이미 Review가 존재하는지 확인
        if (Boolean.TRUE.equals(paymentHistory.getReview())) {
            throw new IllegalStateException("이미 후기가 작성되었습니다.");
        }

        Review review = modelMapper.map(request, Review.class);

        review.setPaymentHistory(paymentHistory);
        reviewRepository.save(review);
        paymentHistory.setReview(true);

        reviewImgService.uploadReviewImg(review.getReviewId(), images);

        return review;
    }

    /**
     * 모든 리뷰 보기
     * @return
     */
    public List<ReviewDto> allReview() {
        List<Review> reviews = reviewRepository.findAll();
       /* if (reviews.isEmpty()) {
            throw new IllegalStateException("리뷰가 없습니다.");
        }*/
        return reviews.stream().map(ReviewDto::new).toList();
    }

    /**
     * 특정 상품의 리뷰 모아보기
     * @param productId
     * @return
     */
    public List<ReviewDto> findReviewByProduct(Long productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));

        List<Review> reviews = reviewRepository.findByPaymentHistoryProduct(product);

        if (reviews.isEmpty()) {
            throw new IllegalStateException(WRITING_NOT_FOUND);
        }

        return reviews.stream().map(ReviewDto::new).toList();
    }

    /**
     * 특정 회원의 리뷰 모아보기
     * @param memberId
     * @return
     */
    public List<ReviewDto> findReviewByUser(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUND));

        List<Review> reviews = reviewRepository.findByPaymentHistoryMember(member);

        if (reviews.isEmpty()) {
            throw new IllegalStateException(WRITING_NOT_FOUND);
        }

        return reviews.stream().map(ReviewDto::new).toList();
    }

    /**
     * 리뷰 상세보기
     * @param reviewId
     * @return
     */
    public ReviewDto reviewDetail(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException(WRITING_NOT_FOUND));

        return new ReviewDto(review);
    }

    /**
     * 리뷰 수정
     * @param updateRequest
     * @param reviewId
     * @return
     */
    public Review updateReview(ReviewCreateDto updateRequest, Long reviewId, Long memberId) {

        Review currentReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException(WRITING_NOT_FOUND));

        Long reviewWriterId = currentReview.getPaymentHistory().getMember().getId();

        Review updatedreview = modelMapper.map(updateRequest, Review.class);

        // 로그인 중인 유저의 memberId 찾기
        Long loginMemberId = MemberAuthorizationUtil.getLoginMemberId();

        if (!loginMemberId.equals(memberId) || !memberId.equals(reviewWriterId)) {
            throw new SecurityException(ACCESS_DENIED);
        }

        currentReview.setReviewContent(updatedreview.getReviewContent());
        currentReview.setRating(updatedreview.getRating());

        return reviewRepository.save(currentReview);
    }

    /**
     * 리뷰 삭제
     * @param reviewId
     */
    public void deleteReview(Long reviewId, Long memberId) {
        Review currentReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException(WRITING_NOT_FOUND));
        Long reviewWriterId = currentReview.getPaymentHistory().getMember().getId();

        PaymentHistory paymentHistory = currentReview.getPaymentHistory();

        paymentHistory.setReview(false);

        // 로그인 중인 유저의 memberId 찾기
        Long loginMemberId = MemberAuthorizationUtil.getLoginMemberId();

        if (!loginMemberId.equals(memberId) || !memberId.equals(reviewWriterId)) {
            throw new SecurityException(ACCESS_DENIED);
        }

        List<ReviewImg> reviewImgList = reviewImgRepository.findByReview_ReviewId(reviewId);

        for (ReviewImg reviewImg : reviewImgList) {
            reviewImgRepository.delete(reviewImg);
            String imagePath = "src/main/resources/static"+reviewImg.getReviewImgPath();
            ReviewImgService.deleteImageFile(imagePath);
        }

        reviewRepository.delete(currentReview);
    }

}
