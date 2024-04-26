package PU.pushop.cart.repository;

import PU.pushop.cart.entity.Cart;
import PU.pushop.members.entity.Member;
import PU.pushop.product.entity.Product;
import PU.pushop.productManagement.entity.ProductManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
    List<Cart> findByMemberId(Long memberId);

    List<Cart> findByCartIdIn(List<Long> cartIds);
    Cart findByMemberIdAndCartId(Long memberId, Long cartId);
    List<Product> findAllProductByCartIdIn(List<Long> cartIds);

    Optional<Cart> findByProductManagement(ProductManagement productManagement);

    Optional<Cart> findByProductManagementAndMember(ProductManagement productManagement, Member member);

}
