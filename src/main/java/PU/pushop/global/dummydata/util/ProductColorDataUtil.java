package PU.pushop.global.dummydata.util;


import PU.pushop.product.entity.ProductColor;
import PU.pushop.product.repository.ProductColorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductColorDataUtil {

    private final ProductColorRepository productColorRepository;

    @Transactional
    public void generateAndSaveProductColorData() {
                createAndSaveProductColor("Red");
                createAndSaveProductColor("Orange");
                createAndSaveProductColor("Yellow");
                createAndSaveProductColor("Green");
                createAndSaveProductColor("Blue");
                createAndSaveProductColor("Navy");
                createAndSaveProductColor("Purple");
                createAndSaveProductColor("Black");
                createAndSaveProductColor("White");
                createAndSaveProductColor("Gray");
                createAndSaveProductColor("Pink");
                createAndSaveProductColor("Ivory");
                createAndSaveProductColor("Beige");
                createAndSaveProductColor("Rainbow");
    }

    private void createAndSaveProductColor(String color) {
        Optional<ProductColor> existingProductColor = productColorRepository.findByColor(color);
        if (existingProductColor.isEmpty()) {
            ProductColor productColor = new ProductColor(color);
            productColorRepository.save(productColor);
        }
    }
}
