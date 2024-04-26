package PU.pushop.global.authorization;

import PU.pushop.members.entity.enums.MemberRole;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static PU.pushop.global.ResponseMessageConstants.ACCESS_DENIED;

@Aspect
@Component
public class AuthorizationAspect {

    @Before("@annotation(requireRole)")
    public void checkRole(RequiresRole requireRole) {
        MemberRole userRole = MemberAuthorizationUtil.getLoginMemberRole();

        boolean hasRequiredRole = false;
        for (MemberRole requiredRole : requireRole.value()) {
            if (userRole == requiredRole) {
                hasRequiredRole = true;
                break;
            }
        }

        if (!hasRequiredRole) {
            throw new SecurityException(ACCESS_DENIED);
        }
    }
}