package PU.pushop.global.authentication.jwt.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageReponseDto {

    private String code;
    private String message;
}
