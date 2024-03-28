package PU.pushop.members.model;


import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.entity.enums.SocialType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OAuthUserDTO {

    private String username;
    private String nickname;
    @Enumerated(value = EnumType.STRING)
    private MemberRole role;
    @Enumerated(value = EnumType.STRING)
    private SocialType socialType;

    public static OAuthUserDTO createOAuthUserDTO(String username, MemberRole role, SocialType socialType) {
        OAuthUserDTO userDTO = new OAuthUserDTO();
        userDTO.setUsername(username);
        userDTO.setRole(role);
        userDTO.setSocialType(socialType);
        return userDTO;
    }
}
