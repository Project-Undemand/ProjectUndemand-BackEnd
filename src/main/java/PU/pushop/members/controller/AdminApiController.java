package PU.pushop.members.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;

// 회원의 refresh token 의 권한이 admin 혹은 seller 임을 확인하는 util 클래스 메서드 추가.
@Controller
public class AdminApiController {

    /**
     * 1. 관리자 권한의 판매자 활성화 기능
     * 2. 관리자 권한의 판매자 회원가입 기능
     * 3. 회원 활성화 기능, 관리자 권한의
     * 4. 회원 비활성화 기능, 관리자 권한의
     * 5. 회원 정보 Update, 관리자 권한의 수정 기능
     */
    @PostMapping("/admin/api/v1/activate")
    public ResponseEntity<?> activateSellerAccount(@CookieValue(name = "refreshAuthorization", required = false) String refreshAuthorization, HttpServletRequest request,
                                    HttpServletResponse response) {
        return ResponseEntity.ok().body("lsdjfa");
    }
}
