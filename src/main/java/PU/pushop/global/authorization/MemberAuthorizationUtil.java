package PU.pushop.global.authorization;

import PU.pushop.global.authentication.jwts.entity.CustomUserDetails;
import PU.pushop.members.entity.enums.MemberRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static PU.pushop.global.ResponseMessageConstants.ACCESS_DENIED;

@Slf4j
public class MemberAuthorizationUtil {

    private MemberAuthorizationUtil() {
        throw new AssertionError();
    }

    private static CustomUserDetails getCustomUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new SecurityException(ACCESS_DENIED);
        }
        return (CustomUserDetails) authentication.getPrincipal();
    }


    public static Long getLoginMemberId() {

        try {
            return getCustomUserDetails().getMemberId();
        } catch (ClassCastException e) {
            throw new SecurityException(ACCESS_DENIED);
        }

    }

    public static MemberRole getLoginMemberRole(){
        try {
            return getCustomUserDetails().getMemberRole();
        } catch (ClassCastException e) {
            throw new SecurityException(ACCESS_DENIED);
        }

    }

    public static void verifyUserIdMatch(Long givenId) {
        System.out.println("givenId = " + givenId);
        Long loginMemberId = getLoginMemberId();
        System.out.println("loginMemberId = " + loginMemberId);

        if (!loginMemberId.equals(givenId)) {
            throw new SecurityException(ACCESS_DENIED);
        }
    }
}
