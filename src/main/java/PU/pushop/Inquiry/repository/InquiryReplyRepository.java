package PU.pushop.Inquiry.repository;

import PU.pushop.Inquiry.entity.InquiryReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryReplyRepository extends JpaRepository<InquiryReply, Long> {
}
