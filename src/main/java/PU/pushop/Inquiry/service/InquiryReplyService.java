package PU.pushop.Inquiry.service;

import PU.pushop.Inquiry.entity.Inquiry;
import PU.pushop.Inquiry.entity.InquiryReply;
import PU.pushop.Inquiry.model.InquiryReplyDto;
import PU.pushop.Inquiry.repository.InquiryReplyRepository;
import PU.pushop.Inquiry.repository.InquiryRepository;
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


    /**
     * 문의 답변 등록
     * @param reply
     * @param inquiryId
     * @return
     */
    @Transactional
    public Long createReply(InquiryReply reply, Long inquiryId) {
        Optional<Inquiry> optionalInquiry = inquiryRepository.findById(inquiryId);
        Inquiry inquiry = optionalInquiry.orElseThrow(() -> new IllegalArgumentException("문의글을 찾을 수 없습니다. inquiryId: " + inquiryId));

        // Inquiry 테이블 의 isResponse -> true
        inquiry.setIsResponse(true);
        // 답변 제목은 문의 제목과 동일
        reply.setReplyTitle(inquiry.getInquiryTitle());

        reply.setInquiry(inquiry);
        inquiryReplyRepository.save(reply);
        inquiryReplyRepository.save(reply);

        return reply.getInquiryReplyId();
    }

    public void deleteReply(Long replyId) {
        InquiryReply existingReply = inquiryReplyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("글을 찾을 수 없습니다."));

        inquiryReplyRepository.delete(existingReply);

    }

}
