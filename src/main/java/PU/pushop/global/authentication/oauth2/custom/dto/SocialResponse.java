package PU.pushop.global.authentication.oauth2.custom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SocialResponse {

    private String accessToken;
    private String refreshToken;
    private String email;

}
