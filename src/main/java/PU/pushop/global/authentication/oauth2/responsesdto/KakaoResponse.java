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
        if (attribute != null && attribute.containsKey("kakao_account")) {
            Object kakaoAccountObj = attribute.get("kakao_account");
            if (kakaoAccountObj instanceof Map) {
                Map<?, ?> kakaoAccount = (Map<?, ?>) kakaoAccountObj;
                Object emailObj = kakaoAccount.get("email");
                if (emailObj instanceof String) {
                    return emailObj.toString();
                }
            }
        }
        return null;
    }

    @Override
    public String getName() {
        if (attribute != null && attribute.containsKey("kakao_account")) {
            Object kakaoAccountObj = attribute.get("kakao_account");
            if (kakaoAccountObj instanceof Map) {
                Map<?, ?> kakaoAccount = (Map<?, ?>) kakaoAccountObj;
                Object profileObj = kakaoAccount.get("profile");
                if (profileObj instanceof Map) {
                    Map<?, ?> profile = (Map<?, ?>) profileObj;
                    // 실제 본명 : nickname
                    Object nicknameObj = profile.get("nickname");
                    if (nicknameObj instanceof String) {
                        return nicknameObj.toString();
                    }
                }
            }
        }
        return null;
    }


}
