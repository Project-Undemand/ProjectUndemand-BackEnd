package PU.pushop.product.repository;

import PU.pushop.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepositoryV1 extends JpaRepository<Product, Long> {

    Optional<Product> findByProductName(String productName);
    Optional<Product> findByProductId(Long productId);

    Page<Product> findAllByOrderByCreatedAtDesc(Pageable pageable); // 최신순

    Page<Product> findAllByOrderByWishListCountDesc(Pageable pageable); // 위시(찜) 많은 순

    Page<Product> findByIsDiscountTrue(Pageable pageable); // 할인목록
    Page<Product> findByIsRecommendTrue(Pageable pageable); // 추천목록




}
