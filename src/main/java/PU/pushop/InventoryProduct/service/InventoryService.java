package PU.pushop.InventoryProduct.service;

import PU.pushop.InventoryProduct.entity.InventoryProduct;
import PU.pushop.InventoryProduct.repository.InventoryProductRepository;
import PU.pushop.global.Exception.UnauthorizedException;
import PU.pushop.global.authentication.jwts.service.CookieService;
import PU.pushop.global.authentication.jwts.utils.JWTUtil;
import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.repository.MemberRepositoryV1;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryProductRepository inventoryProductRepository;
    private final CookieService cookieService;
    private final JWTUtil jwtUtil;
    private final MemberRepositoryV1 memberRepositoryV1;

    // 전체 InventoryProduct 리스트 조회
    public List<InventoryProduct> getAllInventoryProducts(HttpServletRequest request) {
        // 쿠키에서 "refreshAuthorization" 값을 가져 옴
        String refreshAuthorization = cookieService.getRefreshAuthorization(request);
        MemberRole jwtMemberRole = jwtUtil.getRole(refreshAuthorization);

        if (jwtMemberRole != MemberRole.ADMIN) {
            throw new UnauthorizedException("관리자만 접근 가능한 서비스 입니다.");
        }

        return inventoryProductRepository.findAll();
    }

    // 판매자의 InventoryProduct 리스트 조회
    public List<InventoryProduct> getInventoryProductsByManufacturer(HttpServletRequest request, String manufacturer) {
        // 쿠키에서 "refreshAuthorization" 값을 가져 옴
        return inventoryProductRepository.findByProduct_Manufacturer(manufacturer);
    }

    // InventoryProduct의 productStock 업데이트
    public InventoryProduct updateProductStock(Long inventoryProductId, Long productStock) {
        InventoryProduct inventoryProduct = inventoryProductRepository.findById(inventoryProductId)
                .orElseThrow(() -> new RuntimeException("InventoryProduct not found"));
        inventoryProduct.updateProductStock(productStock);
        return inventoryProductRepository.save(inventoryProduct);
    }
}
