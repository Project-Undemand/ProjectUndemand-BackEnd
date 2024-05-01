package PU.pushop.wishList.service;

import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.product.entity.Product;
import PU.pushop.profile.repository.ProfileRepository;
import PU.pushop.wishList.entity.WishList;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.wishList.model.WishListResponseDto;
import PU.pushop.wishList.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class WishListService {
    public final WishListRepository wishListRepository;
    public final ProductRepositoryV1 productRepository;
    public final MemberRepositoryV1 memberRepository;
    public final ProfileRepository profileRepository;

    /**
     * 찜 추가
     * @param product
     * @param member
     * @return
     */
    public Long createWish(Product product, Member member) {
        // 이미 존재하는지 확인
        boolean isAlreadyWished = !wishListRepository.findByProductAndMember(product, member).isEmpty();
        if (isAlreadyWished) {
            throw new IllegalStateException("이미 찜한 상품입니다.");
        }
/*        WishList wishList = new WishList();

        wishList.setMember(member);
        wishList.setProduct(product);
        */
        WishList wishList = new WishList(member, product);

        product.getWishLists().add(wishList);
        member.getWishLists().add(wishList);
        product.setWishListCount((long) product.getWishLists().size());

        productRepository.save(product);
        memberRepository.save(member);

        return wishListRepository.save(wishList).getWishListId();
    }

    /**
     * 찜 삭제
     * @param product
     * @param member
     */
    public void deleteWish(Product product, Member member) {
        WishList existingWishList = wishListRepository.findByProductAndMember(product, member)
                .orElseThrow(() -> new NoSuchElementException("해당 찜목록을 찾을 수 없습니다."));

        wishListRepository.delete(existingWishList);
    }

    /**
     * 내 찜목록 모아보기
     * @param member
     * @return
     */
    public List<WishListResponseDto> myWishList(Member member) {

        List<WishList> wishLists = wishListRepository.findByMember(member);

        List<WishListResponseDto> wishListResponseDtos = new ArrayList<>();

        for (WishList wishList : wishLists) {
            WishListResponseDto wishListResponseDto = new WishListResponseDto(wishList);
            wishListResponseDtos.add(wishListResponseDto);
        }

        return wishListResponseDtos;

    }
}
