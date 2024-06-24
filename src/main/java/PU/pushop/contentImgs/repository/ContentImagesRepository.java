package PU.pushop.contentImgs.repository;

import PU.pushop.contentImgs.entity.ContentImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentImagesRepository extends JpaRepository<ContentImages, Long> {
    List<ContentImages> findByProduct_ProductId(Long productId);
}
