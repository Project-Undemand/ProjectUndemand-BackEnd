package PU.pushop.global.dummydata.util;


import PU.pushop.product.entity.ProductColor;
import PU.pushop.product.repository.ProductColorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductColorDataUtil {

    private final ProductColorRepository productColorRepository;

    @Transactional
    public List<ProductColor>  generateAndSaveProductColorData() {
        return Arrays.asList(
                createAndSaveProductColor("Red"),
                createAndSaveProductColor("Orange"),
                createAndSaveProductColor("Yellow"),
                createAndSaveProductColor("Green"),
                createAndSaveProductColor("Blue"),
                createAndSaveProductColor("Navy"),
                createAndSaveProductColor("Purple"),
                createAndSaveProductColor("Black"),
                createAndSaveProductColor("White"),
                createAndSaveProductColor("Gray"),
                createAndSaveProductColor("Pink"),
                createAndSaveProductColor("Ivory"),
                createAndSaveProductColor("Beige"),
                createAndSaveProductColor("Rainbow")
        );
    }

    private ProductColor createAndSaveProductColor(String color) {
        Optional<ProductColor> existingProductColor = productColorRepository.findByColor(color);
        if (existingProductColor.isEmpty()) {
            ProductColor productColor = new ProductColor(color);
            return productColorRepository.save(productColor);
        } else {
            return existingProductColor.get();
        }
    }
}
