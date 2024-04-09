package PU.pushop.Inquiry.service;

import PU.pushop.Inquiry.entity.Inquiry;
import PU.pushop.Inquiry.entity.InquiryReply;
import PU.pushop.Inquiry.model.InquiryReplyDto;
import PU.pushop.Inquiry.repository.InquiryReplyRepository;
import PU.pushop.Inquiry.repository.InquiryRepository;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class InquiryReplyService {
    private final InquiryReplyRepository inquiryReplyRepository;
    private final InquiryRepository inquiryRepository;
    private final MemberRepositoryV1 memberRepository;

    /**
     * 문의 답변 등록
     * @param replyDto
     * @param inquiryId
     * @return
     */
    @Transactional
    public Long createReply(InquiryReplyDto replyDto, Long inquiryId) {
        // 답변할 문의글
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의글을 찾을 수 없습니다. inquiryId: " + inquiryId));

        InquiryReply reply = new InquiryReply();

        Member member = memberRepository.findById(replyDto.getReplyBy())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. memberId: " + replyDto.getReplyBy()));

        reply.setInquiry(inquiry);
        reply.setReplyBy(member);
        reply.setReplyContent(replyDto.getReplyContent());
        reply.setReplyTitle(inquiry.getInquiryTitle()); // 답변 제목은 문의 제목과 동일

        inquiryReplyRepository.save(reply);

        // Inquiry 테이블 의 isResponse -> true
        inquiry.setIsResponse(true);

        return reply.getInquiryReplyId();
    }

    public void deleteReply(Long replyId) {
        InquiryReply existingReply = inquiryReplyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("글을 찾을 수 없습니다."));

        inquiryReplyRepository.delete(existingReply);

    }

}
