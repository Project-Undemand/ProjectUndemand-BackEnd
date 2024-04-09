package PU.pushop.wishList.service;

import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.product.entity.Product;
import PU.pushop.wishList.entity.WishList;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.wishList.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class WishListService {
    public final WishListRepository wishListRepository;
    public final ProductRepositoryV1 productRepository;
    public final MemberRepositoryV1 memberRepository;

    /**
     * 찜 추가
     * @param product
     * @param member
     * @return
     */
    public WishList createWish(Product product, Member member) {
        // 이미 존재하는지 확인
        Optional<WishList> existingWishList = wishListRepository.findByProductAndMember(product, member);
        if (existingWishList.isPresent()) {
            throw new IllegalStateException("이미 찜한 상품입니다.");
        }
        WishList wishList = new WishList();

        wishList.setMember(member);
        wishList.setProduct(product);

        product.getWishLists().add(wishList);
        productRepository.save(product);
        member.getWishLists().add(wishList);
        memberRepository.save(member);

        wishListRepository.save(wishList);

        return wishList;
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
    public List<WishList> myWishList(Member member) {

        return wishListRepository.findByMember(member);

    }
}
