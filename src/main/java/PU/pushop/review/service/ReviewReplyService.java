package PU.pushop.review.service;

import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.review.entity.Review;
import PU.pushop.review.entity.ReviewReply;
import PU.pushop.review.model.ReviewReplyDto;
import PU.pushop.review.repository.ReviewReplyRepository;
import PU.pushop.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewReplyService {
    private final ReviewReplyRepository reviewReplyRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepositoryV1 memberRepository;

    public Long createReply(ReviewReplyDto replyDto, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다. reviewId: " + reviewId));

        ReviewReply reviewReply = new ReviewReply();

        Member member = memberRepository.findById(replyDto.getReplyBy())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. memberId: " + replyDto.getReplyBy()));

        reviewReply.setReview(review);
        reviewReply.setReplyBy(member);
        reviewReply.setReplyContent(replyDto.getReplyContent());

        reviewReplyRepository.save(reviewReply);

        return reviewReply.getReviewReplyId();
    }

    public void deleteReply(Long replyId) {
        ReviewReply currentReply = reviewReplyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글을 찾을 수 없습니다."));
        reviewReplyRepository.delete(currentReply);

    }


}
