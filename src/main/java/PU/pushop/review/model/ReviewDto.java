package PU.pushop.review.model;

import PU.pushop.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private String reviewTitle;
    private String reviewContent;
    private int rating;

    public ReviewDto(Review review) {
        this(
                review.getReviewTitle(),
                review.getReviewContent(),
                review.getRating()
        );
    }

    public static Review requestForm(ReviewDto request) {
        Review review = new Review();

        review.setReviewTitle(request.getReviewTitle());
        review.setReviewContent(request.getReviewContent());
        review.setRating(request.getRating());

        return review;
    }
}
