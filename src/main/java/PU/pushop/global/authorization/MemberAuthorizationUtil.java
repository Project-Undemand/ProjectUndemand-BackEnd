package PU.pushop.global.authorization;

import PU.pushop.global.authentication.jwts.entity.CustomUserDetails;
import PU.pushop.members.entity.enums.MemberRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static PU.pushop.global.ResponseMessageConstants.ACCESS_DENIED;

public class MemberAuthorizationUtil {

    private MemberAuthorizationUtil() {
        throw new AssertionError();
    }
    public static Long getLoginMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return userDetails.getMemberId();
    }

    public static MemberRole getLoginMemberRole(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return userDetails.getMemberRole();
    }

    public static void verifyUserIdMatch(Long givenId) {
        Long loginMemberId = getLoginMemberId();

        if (!loginMemberId.equals(givenId)) {
            throw new SecurityException(ACCESS_DENIED);
        }
    }
}
