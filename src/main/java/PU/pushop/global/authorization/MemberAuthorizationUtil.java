package PU.pushop.global.authorization;

import PU.pushop.global.authentication.jwts.entity.CustomUserDetails;
import PU.pushop.members.entity.enums.MemberRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static PU.pushop.global.ResponseMessageConstants.*;

@Slf4j
public class MemberAuthorizationUtil {

    private MemberAuthorizationUtil() {
        throw new AssertionError();
    }

    private static CustomUserDetails getCustomUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new SecurityException(ACCESS_DENIED_NO_AUTHENTICATION);
        }
        return (CustomUserDetails) authentication.getPrincipal();
    }


    public static Long getLoginMemberId() {

        try {
            return getCustomUserDetails().getMemberId();
        } catch (ClassCastException e) {
            throw new SecurityException(ACCESS_DENIED_NO_AUTHENTICATION);
        }

    }

    public static MemberRole getLoginMemberRole(){
        try {
            return getCustomUserDetails().getMemberRole();
        } catch (ClassCastException e) {
            throw new SecurityException(ACCESS_DENIED_NO_AUTHENTICATION);
        }

    }

    public static void verifyUserIdMatch(Long givenId) {
        Long loginMemberId = getLoginMemberId();
        MemberRole memberRole = getLoginMemberRole();

        if (!loginMemberId.equals(givenId) && memberRole != MemberRole.ADMIN) {
                throw new SecurityException(ACCESS_DENIED+" : 요청 사용자와 로그인 사용자 불일치");
        }
    }
}
