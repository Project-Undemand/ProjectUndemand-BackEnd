package PU.pushop.members.model;


import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.entity.enums.SocialType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class OAuthUserDTO {

    private String username;
    private String nickname;
    @Enumerated(value = EnumType.STRING)
    private MemberRole role;
    @Enumerated(value = EnumType.STRING)
    private SocialType socialType;
    private String socialId;

    public static OAuthUserDTO createOAuthUserDTO(String username, MemberRole role, SocialType socialType, String socialId) {
        OAuthUserDTO userDTO = new OAuthUserDTO();
        userDTO.setUsername(username);
        userDTO.setRole(role);
        userDTO.setSocialType(socialType);
        userDTO.setSocialId(socialId);
        return userDTO;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("username", this.username);
        map.put("nickname", this.nickname);
        map.put("role", this.role.toString());
        map.put("socialType", this.socialType.toString());
        map.put("socialId", this.socialId);
        return map;
    }
}
