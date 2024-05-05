package PU.pushop.global.authorization;

import PU.pushop.members.entity.enums.MemberRole;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static PU.pushop.global.ResponseMessageConstants.ACCESS_DENIED;

@Aspect
@Component
public class AuthorizationAspect {

    @Value("${custom.requires-role.enabled:true}") // 프로퍼티 값을 가져옴
    private boolean requiresRoleEnabled;

    @Before("@annotation(requireRole)")
    public void checkRole(RequiresRole requireRole) {
        if (!requiresRoleEnabled) {
            // requiresRoleEnabled가 false이면 어노테이션 체크를 스킵
            return;
        }

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