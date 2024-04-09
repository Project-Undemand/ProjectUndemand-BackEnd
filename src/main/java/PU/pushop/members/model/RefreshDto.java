package PU.pushop.members.model;

import PU.pushop.members.entity.Member;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RefreshDto {

    private Member member;

    private String refreshToken;

    private LocalDateTime expirationDate;

    public RefreshDto(Member member, String refreshToken, LocalDateTime expirationDate) {
        this.member = member;
        this.refreshToken = refreshToken;
        this.expirationDate = expirationDate;
    }

    public static RefreshDto createRefreshDto(Member member, String newRefreshToken, LocalDateTime expirationDateTime) {
        return new RefreshDto(member, newRefreshToken, expirationDateTime);
    }
}
