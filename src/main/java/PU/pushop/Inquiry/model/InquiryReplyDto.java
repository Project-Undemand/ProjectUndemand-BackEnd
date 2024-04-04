package PU.pushop.Inquiry.model;

import PU.pushop.Inquiry.entity.InquiryReply;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryReplyDto {
    private Long inquiryReplyId;
    private Long inquiryId;
    private Long replyBy;
    private String replyTitle;
    private String replyContent;
    private LocalDate createdAt;

    public InquiryReplyDto(InquiryReply inquiryReply) {
        this(
                inquiryReply.getInquiryReplyId(),
                inquiryReply.getInquiry().getInquiryId(),
                inquiryReply.getReplyBy().getId(),
                inquiryReply.getReplyTitle(),
                inquiryReply.getReplyContent(),
                inquiryReply.getCreatedAt()

                );
    }
}
