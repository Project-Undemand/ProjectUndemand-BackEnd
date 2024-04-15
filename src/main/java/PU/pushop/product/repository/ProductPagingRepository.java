package PU.pushop.product.repository;

import PU.pushop.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProductPagingRepository extends PagingAndSortingRepository<Product, Long> {
    Page<Product> findAllByOrderByCreatedAtDesc(Pageable pageable); // 최신순
    Page<Product> findAllByOrderByWishListCountDesc(Pageable pageable); // 위시(찜) 많은 순

    Page<Product> findByIsDiscountTrue(Pageable pageable); // 할인목록
    Page<Product> findByIsRecommendTrue(Pageable pageable); // 추천목록
}
