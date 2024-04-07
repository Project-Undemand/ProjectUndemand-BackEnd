package PU.pushop.order.repository;

import PU.pushop.order.entity.Orders;
import PU.pushop.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders,Long> {
    Optional<Orders> findByMemberId(Long memberId);

}
