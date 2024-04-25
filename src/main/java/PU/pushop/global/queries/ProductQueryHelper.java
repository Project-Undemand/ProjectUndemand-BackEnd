package PU.pushop.global.queries;

import PU.pushop.product.entity.QProduct;
import com.querydsl.core.types.OrderSpecifier;

public class ProductQueryHelper {
    public static OrderSpecifier<?> getOrderSpecifier(String order, QProduct product) {
        if (order == null) {
            // order가 null인 경우 기본 정렬 기준으로 처리
            return product.createdAt.desc();
        }
        switch (order) {
            case "new":
                return product.createdAt.desc();
            case "best":
                return product.wishListCount.desc();
            case "low-price":
                return product.price.asc();
            case "high-price":
                return product.price.desc();
            case "high-discount-rate":
                return product.discountRate.desc();
            default:
                return product.createdAt.desc();
        }
    }
}
