package PU.pushop.Inquiry.controller;

import PU.pushop.Inquiry.entity.InquiryReply;
import PU.pushop.Inquiry.service.InquiryReplyService;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inquiry/reply")
@RequiredArgsConstructor
public class InquiryReplyController {
    private final InquiryReplyService replyService;
    private final MemberRepositoryV1 memberRepository;


    @Data
    static class ReplyRequest {
        private Long replyBy;
        private String content;

    }

    private InquiryReply ReplyFormRequest(ReplyRequest request) {
        InquiryReply reply = new InquiryReply();
        Member replyBy = memberRepository.findById(request.getReplyBy()).orElse(null);
        reply.setReplyBy(replyBy);
        reply.setReplyContent(request.content);

        return reply;
    }

    /**
     * 문의 답변 등록
     *
     * @param request
     * @param inquiryId
     * @return
     */
    @PostMapping("/new/{inquiryId}")
    public ResponseEntity<?> createReply(@Valid @RequestBody ReplyRequest request, @PathVariable Long inquiryId) {
        InquiryReply reply = ReplyFormRequest(request);
        Long createdId = replyService.createReply(reply, inquiryId);

        return ResponseEntity.ok(createdId);

    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<?> deleteInquiry(@PathVariable Long replyId) {
        replyService.deleteReply(replyId);
        return ResponseEntity.ok().build();
    }
}
