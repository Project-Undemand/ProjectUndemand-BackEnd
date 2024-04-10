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

        String body = "<div>"
                + "<h1> PUSHOP </h1>"
                + "<h1> 문의주셔서 감사합니다.<br>아래 답변 내용을 확인해주세요. </h1>"

                + "<a href='"
                // 여기에 해당 문의 보기 프론트 링크
                + "'><b>문의내용 보러가기</b></a>"
                + "<a href='"
                // 여기에 문의 작성 화면으로 가는 프론트 링크
                + "'> / 추가 문의하기</a>"

                + "<br><br><br><hr><br><br><br> "

                + "<b>" + reply.getReplyContent() + "</b>"
                + "<br><br><br><br><br><br> "
                + "["+inquiry.getProduct().getProductName()+ "] - "+inquiry.getInquiryTitle()
                + "<br>" + inquiry.getInquiryContent()
                + "<br><br><br><hr><br><br><br> "
                + "<p>이용해주셔서 감사합니다.<p>"
                + "</div>";
        message.setText(body, "utf-8", "html");// 내용, charset 타입, subtype
        message.setFrom(new InternetAddress("gokorea1214@naver.com", "PU_ADMIN")); // 보내는 사람
        mailSender.send(message);
    }

}
