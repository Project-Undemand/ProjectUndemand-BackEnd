package PU.pushop.members.model;


import PU.pushop.members.entity.enums.MemberRole;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OAuthUserDTO {

    private String username;
    private String nickname;
    private MemberRole role;

    public static OAuthUserDTO createOAuthUserDTO(String nickname, String username, MemberRole role) {
        OAuthUserDTO userDTO = new OAuthUserDTO();
        userDTO.setNickname(nickname);
        userDTO.setUsername(username);
        userDTO.setRole(role);
        return userDTO;
    }
}
