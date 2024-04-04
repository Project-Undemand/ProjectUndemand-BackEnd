package PU.pushop.global.authentication.oauth2.responsesdto;

import java.util.HashMap;
import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }


    // Long 값을 Map으로 변환하는 메서드
    private Map<String, Object> convertLongToMap(Long idValue) {
        // 여기서는 예를 들어 Long 값을 그대로 사용하거나, 필요에 따라 Map으로 변환하는 로직을 구현할 수 있습니다.
        // 예시로 빈 맵을 반환합니다.
        return new HashMap<>();
    }



    @Override
    public String getProvider() {

        return "kakao";
    }

    @Override
    public String getProviderId() {
        // "id" 값 가져오기
        return attribute != null && attribute.containsKey("id") ? attribute.get("id").toString() : null;
    }


    @Override
    public String getEmail() {
        // "email" 값 가져오기
        if (attribute != null && attribute.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
            if (kakaoAccount.containsKey("email")) {
                return kakaoAccount.get("email").toString();
            }
        }
        return null;
    }

    @Override
    public String getName() {
        // "nickname" 값 가져오기
        if (attribute != null && attribute.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
            if (kakaoAccount.containsKey("profile")) {
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                if (profile.containsKey("nickname")) {
                    return profile.get("nickname").toString();
                }
            }
        }
        return null;
    }

}
