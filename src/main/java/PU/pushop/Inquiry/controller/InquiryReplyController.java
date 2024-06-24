package PU.pushop.Inquiry.controller;

import PU.pushop.Inquiry.model.InquiryReplyDto;
import PU.pushop.Inquiry.service.InquiryReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static PU.pushop.global.ResponseMessageConstants.DELETE_SUCCESS;

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
    public ResponseEntity<String> createReply(@Valid @RequestBody InquiryReplyDto replyRequest, @PathVariable Long inquiryId) throws Exception {
        Long createdId = replyService.createReply(replyRequest, inquiryId);

        return ResponseEntity.status(HttpStatus.CREATED).body("답변 등록 완료 : "+createdId);

    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<String> deleteInquiry(@PathVariable Long replyId) {
        replyService.deleteReply(replyId);
        return ResponseEntity.ok().body(DELETE_SUCCESS);
    }
}
