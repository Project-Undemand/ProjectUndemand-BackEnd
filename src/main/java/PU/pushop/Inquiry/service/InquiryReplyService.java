package PU.pushop.Inquiry.service;

import PU.pushop.Inquiry.entity.Inquiry;
import PU.pushop.Inquiry.entity.InquiryReply;
import PU.pushop.Inquiry.model.InquiryReplyDto;
import PU.pushop.Inquiry.repository.InquiryReplyRepository;
import PU.pushop.Inquiry.repository.InquiryRepository;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class InquiryReplyService {
    private final InquiryReplyRepository inquiryReplyRepository;
    private final InquiryRepository inquiryRepository;
    private final MemberRepositoryV1 memberRepository;
    private final JavaMailSender mailSender;

    /**
     * 문의 답변 등록
     * @param replyDto
     * @param inquiryId
     * @return
     */
    @Transactional
    public Long createReply(InquiryReplyDto replyDto, Long inquiryId) throws Exception {
        // 답변할 문의글
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new NoSuchElementException("문의글을 찾을 수 없습니다. inquiryId: " + inquiryId));

        InquiryReply reply = new InquiryReply();

        Member member = memberRepository.findById(replyDto.getReplyBy())
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다. memberId: " + replyDto.getReplyBy()));

        reply.setInquiry(inquiry);
        reply.setReplyBy(member);
        reply.setReplyContent(replyDto.getReplyContent());
        reply.setReplyTitle(inquiry.getInquiryTitle()); // 답변 제목은 문의 제목과 동일

        inquiryReplyRepository.save(reply);

        // Inquiry 테이블 의 isResponse -> true
        inquiry.setIsResponse(true);
        // 답변 메일
        sendReplyNotice(inquiry, reply);

        return reply.getInquiryReplyId();
    }

    public void deleteReply(Long replyId) {
        InquiryReply existingReply = inquiryReplyRepository.findById(replyId)
                .orElseThrow(() -> new NoSuchElementException("글을 찾을 수 없습니다."));

        inquiryReplyRepository.delete(existingReply);

    }

    public void sendReplyNotice(Inquiry inquiry, InquiryReply reply) throws MessagingException, UnsupportedEncodingException {
        String mailAddress = inquiry.getEmail();
        MimeMessage message = mailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, mailAddress);
        message.setSubject("[PUSHOP] 문의하신 상품에 대한 답변이 도착했습니다."); // 제목

        String body = "<div style='max-width: 1000px;'>"
            + "<div style='width: 85%; margin: 0 auto; color: rgb(7, 145, 163);'>"
            + "<h1 style='left:10vw'>PUSHOP</h1>"
            + "<h1>문의주셔서 감사합니다.<br>아래 답변 내용을 확인해주세요.</h1>"
            + "<a href='" + //문의글 보러가기 링크
                "'><b>문의내용 보러가기</b> / 추가 문의하기</a></div>"

            + "<div style='width: 85%; background-color: rgb(239, 247, 255);  margin: 40px auto;'><p style='padding: 40px 50px; overflow: auto; width: auto; height: 100%; min-height: 180px; font-weight: bold;'>"
            + reply.getReplyContent()
            + "</p></div>"

            + "<div style='width: 85%; margin: 0 auto; margin-bottom: 150px; '><hr style=' margin-bottom: 50px; border-style: dashed; border-width: 1px 0px 0px 0px; border-color: rgb(0, 0, 0);'>"
            +"["+inquiry.getProduct().getProductName()+ "] - "+inquiry.getInquiryTitle()+"<br>"
            + inquiry.getInquiryContent()
            +" </div> </div>";

        message.setText(body, "utf-8", "html");// 내용, charset 타입, subtype
        message.setFrom(new InternetAddress("gokorea1214@naver.com", "PU_ADMIN")); // 보내는 사람
        mailSender.send(message);
    }

}
