package PU.pushop.review.controller;

import PU.pushop.review.model.ReviewReplyDto;
import PU.pushop.review.service.ReviewReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/review/reply")
@RequiredArgsConstructor
public class ReviewReplyController {
    private final ReviewReplyService replyService;

    @PostMapping("/new/{reviewId}")
    public ResponseEntity<?> createReply(@Valid @RequestBody ReviewReplyDto request, @PathVariable Long reviewId) {
        ReviewReplyDto replyDto = ReviewReplyDto.ReplyFormRequest(request);
        Long createdId = replyService.createReply(replyDto, reviewId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdId);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<?> deleteReply(@PathVariable Long replyId) {
        replyService.deleteReply(replyId);
        return ResponseEntity.ok().body("삭제 완료");
    }
}
