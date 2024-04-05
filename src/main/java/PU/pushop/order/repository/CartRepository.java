package PU.pushop.order.repository;

import PU.pushop.order.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
    List<Cart> findByMemberId(Long memberId);

    List<Cart> findByCartIdIn(List<Long> cartIds);
    Cart findByMemberIdAndCartId(Long memberId, Long cartId);
}
