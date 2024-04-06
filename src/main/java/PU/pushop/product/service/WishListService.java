package PU.pushop.product.service;

import PU.pushop.members.entity.Member;
import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.WishList;
import PU.pushop.product.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class WishListService {
    public final WishListRepository wishListRepository;


    public WishList createWish(Product product, Member member) {
        // 이미 존재하는지 확인
        Optional<WishList> existingWishList = wishListRepository.findByProductAndMember(product, member);
        if (existingWishList.isPresent()) {
            throw new IllegalStateException("이미 찜한 상품입니다.");
        }
        WishList wishList = new WishList();

        wishList.setMember(member);
        wishList.setProduct(product);

        wishListRepository.save(wishList);
        return wishList;
    }

    public void deleteWish(Product product, Member member) {
        WishList existingWishList = wishListRepository.findByProductAndMember(product, member)
                .orElseThrow(() -> new NoSuchElementException("해당 찜목록을 찾을 수 없습니다."));

        wishListRepository.delete(existingWishList);
    }
}
