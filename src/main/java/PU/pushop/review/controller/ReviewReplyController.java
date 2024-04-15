package PU.pushop.review.controller;

import PU.pushop.review.model.ReviewReplyDto;
import PU.pushop.review.service.ReviewReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static PU.pushop.global.ResponseMessageConstants.DELETE_SUCCESS;

@RestController
@RequestMapping("/api/v1/review/reply")
@RequiredArgsConstructor
public class ReviewReplyController {
    private final ReviewReplyService replyService;

    @PostMapping("/new/{reviewId}")
    public ResponseEntity<String> createReply(@Valid @RequestBody ReviewReplyDto request, @PathVariable Long reviewId) {
        ReviewReplyDto replyDto = ReviewReplyDto.ReplyFormRequest(request);
        Long createdId = replyService.createReply(replyDto, reviewId);

        return ResponseEntity.status(HttpStatus.CREATED).body("리뷰 답변 등록 완료 : "+createdId);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<String> deleteReply(@PathVariable Long replyId) {
        replyService.deleteReply(replyId);
        return ResponseEntity.ok().body(DELETE_SUCCESS);
    }
}
