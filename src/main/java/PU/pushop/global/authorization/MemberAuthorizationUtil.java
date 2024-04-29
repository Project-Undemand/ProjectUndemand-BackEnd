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
    public static Long getLoginMemberId() {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                throw new SecurityException(ACCESS_DENIED);
            }
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            return userDetails.getMemberId();
        } catch (ClassCastException e) {
            throw new SecurityException(ACCESS_DENIED);
        }

    }

    public static MemberRole getLoginMemberRole(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null) {
                throw new SecurityException(ACCESS_DENIED);
            }
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            return userDetails.getMemberRole();
        } catch (ClassCastException e) {
            throw new SecurityException(ACCESS_DENIED);
        }

    }

    public static void verifyUserIdMatch(Long givenId) {
        Long loginMemberId = getLoginMemberId();

        if (!loginMemberId.equals(givenId)) {
            throw new SecurityException(ACCESS_DENIED);
        }
    }
}
