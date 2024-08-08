package PU.pushop.global.dummydata.util;

import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.payment.repository.PaymentRepository;
import PU.pushop.review.entity.Review;
import PU.pushop.review.repository.ReviewRepository;
import PU.pushop.reviewImg.ReviewImg;
import PU.pushop.reviewImg.ReviewImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewDataUtil {

    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final MemberRepositoryV1 memberRepositoryV1;

    @Transactional
    public void createReviews() {
        List<String> emails = List.of("user1@pushop.com", "user2@pushop.com", "user3@pushop.com");

        Random random = new Random();
        for (String email : emails) {
            Member member = memberRepositoryV1.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Member not found"));

            List<PaymentHistory> paymentHistories = paymentRepository.findAll()
                    .stream()
                    .filter(ph -> ph.getMember().equals(member))
                    .toList();

            if (paymentHistories.size() > 6) {
                List<PaymentHistory> selectedPaymentHistories = random.ints(0, paymentHistories.size())
                        .distinct()
                        .limit(6)
                        .mapToObj(paymentHistories::get)
                        .toList();

                for (int i = 0; i < selectedPaymentHistories.size(); i++) {
                    PaymentHistory paymentHistory = selectedPaymentHistories.get(i);
                    int rating = random.nextInt(5) + 1; // 1에서 5까지의 무작위 정수
                    String reviewContent = "This is a review content for payment history ID " + paymentHistory.getId();
                    String reviewTitle = "Review Title for payment history ID " + paymentHistory.getId();

                    Review review = new Review(paymentHistory, reviewContent, rating);
                    reviewRepository.save(review);

                    // Adding review images for 3 out of 6 reviews
                    if (i < 3) {
                        ReviewImg reviewImg = new ReviewImg(review, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSZU3vEXBqPNiMoEm8-sWW20PnareV5kBfc-w&s");
                        reviewImgRepository.save(reviewImg);
                    }
                }
            }
        }
    }
}
