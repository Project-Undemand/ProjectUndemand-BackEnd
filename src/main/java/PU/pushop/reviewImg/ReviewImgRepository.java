package PU.pushop.reviewImg;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImgRepository extends JpaRepository<ReviewImg, Long> {

    List<ReviewImg> findByReview_ReviewId(Long reviewId);
}
