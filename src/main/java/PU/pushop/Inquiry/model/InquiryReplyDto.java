package PU.pushop.Inquiry.model;

import PU.pushop.Inquiry.controller.InquiryReplyController;
import PU.pushop.Inquiry.entity.InquiryReply;
import PU.pushop.members.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryReplyDto {
    private Long inquiryReplyId;
    private Long inquiryId;
    private Long replyBy;
    private String replyTitle;
    private String replyContent;
    private LocalDateTime createdAt;

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

    public static InquiryReplyDto ReplyFormRequest(InquiryReplyDto request) {
        InquiryReplyDto replyDto = new InquiryReplyDto();

        replyDto.setReplyBy(request.replyBy);
        replyDto.setReplyContent(request.replyContent);

        return replyDto;
    }
}
