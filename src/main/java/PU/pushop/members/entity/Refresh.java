package PU.pushop.members.entity;


import PU.pushop.members.model.RefreshDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "REFRESH")
public class Refresh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REFRESH_ID")
    private Long id;

    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

//    @Column(columnDefinition = "TIMESTAMP") // MySQL의 경우
    @Column(columnDefinition = "DATETIME", name = "REFRESH_EXPIRATION") // H2Database의 경우
    private LocalDateTime expiration;

    public Refresh(Member member, String refreshToken, LocalDateTime expiration) {
        this.member = member;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

    public void updateRefreshToken(RefreshDto refreshDto) {
        this.refreshToken = refreshDto.getRefreshToken();
        this.expiration = refreshDto.getExpirationDate();
    }
}
