package PU.pushop.product.repository;

import PU.pushop.product.entity.ProductThumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductThumbnailRepositoryV1 extends JpaRepository<ProductThumbnail, Long> {
    List<ProductThumbnail> findByProduct_ProductId(Long productId);
    Optional<ProductThumbnail> findByThumbnailId(Long thumbnailId);

}
