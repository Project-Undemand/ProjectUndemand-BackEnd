package PU.pushop.product.repository;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.enums.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepositoryV1 extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN FETCH p.productThumbnails")
    List<Product> findAllWithThumbnails();

    Optional<Product> findByProductId(Long productId);

    Page<Product> findAllByOrderByCreatedAtDesc(Pageable pageable); // 최신순

    Page<Product> findAllByOrderByWishListCountDesc(Pageable pageable); // 위시(찜) 많은 순

    Page<Product> findByIsDiscountTrue(Pageable pageable); // 할인목록
    Page<Product> findByIsRecommendTrue(Pageable pageable); // 추천목록

    Page<Product> findByProductType(ProductType productType, Pageable pageable);

    Page<Product> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime date, Pageable pageable);
    Page<Product> findByWishListCountGreaterThanOrderByWishListCountDesc(int count, Pageable pageable);

}
