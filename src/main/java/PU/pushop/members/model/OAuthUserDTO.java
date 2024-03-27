package PU.pushop.members.model;


import PU.pushop.members.entity.enums.SocialType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OAuthUserDTO {

    private String username;
    private String nickname;
    private SocialType role;

    public static OAuthUserDTO createOAuthUserDTO(String username, SocialType role) {
        OAuthUserDTO userDTO = new OAuthUserDTO();
        userDTO.setUsername(username);
        userDTO.setRole(role);
        return userDTO;
    }
}
