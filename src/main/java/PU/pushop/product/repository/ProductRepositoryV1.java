package PU.pushop.product.repository;

import PU.pushop.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepositoryV1 extends JpaRepository<Product, Long> {

    Optional<Product> findByProductName(String productName);
    Optional<Product> findByProductId(Long productId);

    List<Product> findAllByOrderByWishListCountDesc(); // 위시(찜) 많은 순
    List<Product> findAllByOrderByCreatedAtDesc(); // 최신순
    List<Product> findByIsDiscountTrue(); // 할인목록
    List<Product> findByIsRecommendTrue(); // 추천목록
}
