package PU.pushop.review.model;

import PU.pushop.Inquiry.model.InquiryReplyDto;
import PU.pushop.review.entity.ReviewReply;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewReplyDto {
    private Long reviewReplyId;
    private Long reviewId;
    private Long replyBy;
    private String replyContent;
    private LocalDate createdAt;

    public ReviewReplyDto(ReviewReply reviewReply) {
        this(
                reviewReply.getReviewReplyId(),
                reviewReply.getReview().getReviewId(),
                reviewReply.getReplyBy().getId(),
                reviewReply.getReplyContent(),
                reviewReply.getCreatedAt()
        );
    }

    public static ReviewReplyDto ReplyFormRequest(ReviewReplyDto request) {
        ReviewReplyDto replyDto = new ReviewReplyDto();

        replyDto.setReplyBy(request.replyBy);
        replyDto.setReplyContent(request.replyContent);

        return replyDto;
    }
}
