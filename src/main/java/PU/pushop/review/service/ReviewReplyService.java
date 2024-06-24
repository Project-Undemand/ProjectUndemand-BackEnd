package PU.pushop.review.service;

import PU.pushop.global.authorization.RequiresRole;
import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.review.entity.Review;
import PU.pushop.review.entity.ReviewReply;
import PU.pushop.review.model.ReviewReplyDto;
import PU.pushop.review.repository.ReviewReplyRepository;
import PU.pushop.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static PU.pushop.global.ResponseMessageConstants.MEMBER_NOT_FOUND;
import static PU.pushop.global.ResponseMessageConstants.WRITING_NOT_FOUND;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ReviewReplyService {
    private final ReviewReplyRepository reviewReplyRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepositoryV1 memberRepository;

    /**
     * 리뷰 댓글 작성
     * @param replyDto
     * @param reviewId
     * @return
     */
    @RequiresRole({MemberRole.ADMIN, MemberRole.SELLER})
    public Long createReply(ReviewReplyDto replyDto, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("해당 리뷰를 찾을 수 없습니다. reviewId: " + reviewId));

//        ReviewReply reviewReply = new ReviewReply();

        Member member = memberRepository.findById(replyDto.getReplyBy())
                .orElseThrow(() -> new NoSuchElementException(MEMBER_NOT_FOUND));

        ReviewReply reviewReply = new ReviewReply(review, member, replyDto.getReplyContent());

        reviewReplyRepository.save(reviewReply);

        return reviewReply.getReviewReplyId();
    }

    /**
     * 리뷰 댓글 삭제
     * @param replyId
     */
    @RequiresRole({MemberRole.ADMIN, MemberRole.SELLER})
    public void deleteReply(Long replyId) {
        ReviewReply currentReply = reviewReplyRepository.findById(replyId)
                .orElseThrow(() -> new NoSuchElementException(WRITING_NOT_FOUND));
        reviewReplyRepository.delete(currentReply);

    }


}
