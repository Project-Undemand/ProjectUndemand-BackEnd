package PU.pushop.review.model;

import PU.pushop.review.entity.Review;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateDto {
    private String reviewContent;
    @Min(value = 1, message = "별점은 1 이상 5 이하의 정수만 가능합니다.")
    @Max(value = 5, message = "별점은 1 이상 5 이하의 정수만 가능합니다.")
    private int rating;

    public ReviewCreateDto(Review review) {
        this(
                review.getReviewContent(),
                review.getRating()

        );
    }
}
