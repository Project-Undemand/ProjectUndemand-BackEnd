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

    private Long memberId;
    private String username;
    private String email;
    private MemberRole role;
    private SocialType socialType;
    private String socialId;
    private boolean isCertify;

    public static OAuthUserDTO createOAuthUserDTO(String email, String username, MemberRole role, SocialType socialType, String socialId, boolean isCertifty) {
        OAuthUserDTO userDTO = new OAuthUserDTO();
        userDTO.setEmail(email);
        userDTO.setUsername(username);
        userDTO.setRole(role);
        userDTO.setSocialType(socialType);
        userDTO.setSocialId(socialId);
        userDTO.setCertify(isCertifty);
        return userDTO;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("username", this.username);
        map.put("nickname", this.email);
        map.put("role", this.role.toString());
        map.put("socialType", this.socialType.toString());
        map.put("socialId", this.socialId);
        map.put("is_certify_by_email", this.isCertify);
        return map;
    }
}
