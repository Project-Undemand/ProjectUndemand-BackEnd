package PU.pushop.Inquiry.controller;

import PU.pushop.Inquiry.entity.InquiryReply;
import PU.pushop.Inquiry.model.InquiryReplyDto;
import PU.pushop.Inquiry.service.InquiryReplyService;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/inquiry/reply")
@RequiredArgsConstructor
public class InquiryReplyController {
    private final InquiryReplyService replyService;

    /**
     * 문의 답변 등록
     *
     * @param replyRequest
     * @param inquiryId
     * @return
     */
    @PostMapping("/new/{inquiryId}")
    public ResponseEntity<?> createReply(@Valid @RequestBody InquiryReplyDto replyRequest, @PathVariable Long inquiryId) throws Exception {
        Long createdId = replyService.createReply(replyRequest, inquiryId);

        return ResponseEntity.ok(createdId);

    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<?> deleteInquiry(@PathVariable Long replyId) {
        replyService.deleteReply(replyId);
        return ResponseEntity.ok().build();
    }
}
