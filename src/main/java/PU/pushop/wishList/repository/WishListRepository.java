package PU.pushop.wishList.repository;

import PU.pushop.members.entity.Member;
import PU.pushop.product.entity.Product;
import PU.pushop.wishList.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {
    Optional<WishList> findByProductAndMember(Product product, Member member);

    List<WishList> findByMember(Member member);

}
