package PU.pushop.wishList.controller;

import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.product.entity.Product;
import PU.pushop.wishList.entity.WishList;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.wishList.model.WishListResponseDto;
import PU.pushop.wishList.service.WishListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishListController {
    private final WishListService wishListService;
    private final ProductRepositoryV1 productRepository;
    private final MemberRepositoryV1 memberRepository;

    /**
     * 찜하기
     *
     * @param productId
     * @param memberId
     * @return
     */
    @PostMapping("/{productId}/{memberId}")
    public ResponseEntity<?> createWish(@Valid @PathVariable Long productId, @PathVariable Long memberId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));

            Long wishListId = wishListService.createWish(product, member);
            return ResponseEntity.status(HttpStatus.CREATED).body("찜 완료. Id : "+wishListId);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * 찜 삭제
     *
     * @param productId
     * @param memberId
     * @return
     */
    @DeleteMapping("/{productId}/{memberId}")
    public String deleteWish(@Valid @PathVariable Long productId, @PathVariable Long memberId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));
        wishListService.deleteWish(product, member);
        return "삭제완료";
    }

    /**
     * 내 찜목록 모아보기
     * @param memberId
     * @return
     */
    @GetMapping("/{memberId}")
    public List<WishListResponseDto> myWishList(@Valid @PathVariable Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));

        return wishListService.myWishList(member);


    }
}

