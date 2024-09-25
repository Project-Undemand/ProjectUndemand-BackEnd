package PU.pushop.members.service;

import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.Refresh;
import PU.pushop.members.model.RefreshDto;
import PU.pushop.members.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefreshService {

    private final RefreshRepository refreshRepository;
    private static final Long refreshTokenExpirationPeriod = 3600L * 24 * 7; // 7일


    /**
     * [Refresh 토큰 - DB에서 관리합니다.] 리프레쉬 토큰 관리권한이 서버에 있습니다.
     * 로그인에 성공했을 때, 이미 가지고 있던 리프레쉬 토큰 or 처음 로그인한 유저에 대해 리프레쉬 토큰을 DB에 업데이트합니다.
     * @param member 회원의 PK로, member의 refresh Token를 조회.
     * @param newRefreshToken
     */
    public void saveOrUpdateRefreshEntity(Member member, String newRefreshToken) {
        // 멤버의 PK 식별자로, refresh 토큰을 가져옵니다.
        Optional<Refresh> existedRefresh = refreshRepository.findByMemberId(member.getId());
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(refreshTokenExpirationPeriod);
        // 로그인 이메일과 같은 이메일을 가지고 있는 Refresh 엔티티에 대해서, refresh 값을 새롭게 업데이트해줌
        if (existedRefresh.isPresent()) {
            Refresh refreshEntity = existedRefresh.get();
            // Dto 를 통해서, 새롭게 생성한 RefreshToken 값, 유효기간 등을 받아줍니다.
            RefreshDto refreshDto = RefreshDto.createRefreshDto(newRefreshToken, expirationDateTime);
            // Dto 정보들로 기존에 있던 Refresh 엔티티를 업데이트 후 저장합니다.
            refreshEntity.updateRefreshToken(refreshDto);
            refreshRepository.save(refreshEntity);
        } else {
            // 완전히 새로운 리프레시 토큰을 생성 후 저장
            Refresh newRefreshEntity = new Refresh(member, newRefreshToken, expirationDateTime);
            refreshRepository.save(newRefreshEntity);
        }
    }
}
