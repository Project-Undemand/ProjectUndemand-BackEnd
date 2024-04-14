package PU.pushop.payment.repository;

import PU.pushop.payment.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentHistory, Long> {
    List<PaymentHistory> findByMemberId(Long memberId);
}
