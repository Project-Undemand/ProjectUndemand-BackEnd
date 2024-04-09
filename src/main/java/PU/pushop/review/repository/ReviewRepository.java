package PU.pushop.review.repository;

import PU.pushop.members.entity.Member;
import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.product.entity.Product;
import PU.pushop.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPaymentHistoryProduct(Product product);
    List<Review> findByPaymentHistoryMember(Member member);
    Optional<Review> findByPaymentHistory(PaymentHistory paymentHistory);
}
