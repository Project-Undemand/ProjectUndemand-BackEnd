package PU.pushop.Inquiry.repository;

import PU.pushop.Inquiry.entity.Inquiry;
import PU.pushop.Inquiry.entity.enums.InquiryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByProduct_ProductId(Long productId);

    @Modifying
    @Query("UPDATE Inquiry i SET i.inquiryType = :inquiryType, i.inquiryTitle = :inquiryTitle WHERE i.inquiryId = :inquiryId")
    void updateInquiryFields(@Param("inquiryId") Long inquiryId, @Param("inquiryType") InquiryType inquiryType, @Param("inquiryTitle") String inquiryTitle);
}
