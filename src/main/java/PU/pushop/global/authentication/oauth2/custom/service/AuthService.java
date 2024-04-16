package PU.pushop.global.authentication.oauth2.custom.service;

import PU.pushop.global.authentication.oauth2.custom.dto.KakaoTokenDto;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Transactional
    public KakaoTokenDto getKakaoAccessToken(String code) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//        httpHeaders.add("Authorization", "Bearer " + code);
        return new KakaoTokenDto();
    }
}
