package PU.pushop;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class PushopApplication {

	public static void main(String[] args) {
		SpringApplication.run(PushopApplication.class, args);
	}

	@Bean
	Hibernate6Module hibernate6Module() {
		return new Hibernate6Module();
	}

	// Method to generate the SQL statements
	public static void generateSQLStatements() {
		StringBuilder sql = new StringBuilder();

		for (int i = 6; i <= 500; i++ ) {
			// assume that you have only 5 images and product_type cycles between MAN and WOMAN.
			String productName = String.format("Product %d", i);
			String productType = i % 2 == 0 ?  "MAN" : "WOMAN";
			int price = 10000 * (i % 5 + 1); // just an example for price
			String productInfo = String.format("This is a %s product", productType.toLowerCase());
			String manufacturer = String.format("Manufacturer %d", i);
			boolean isDiscount = i % 2 == 0;
			boolean isRecommend = i % 3 == 0;
			String imagePath = String.format("/images/products/product%d.png", i % 5 + 1);

			sql.append(String.format("INSERT INTO products_table "
							+ "(product_name, product_type, price, product_info, manufacturer, is_discount, is_recommend) "
							+ "VALUES ('%s', '%s', %d, '%s', '%s', %b, %b);\n",
					productName, productType, price, productInfo, manufacturer, isDiscount, isRecommend));

			sql.append(String.format("INSERT INTO product_thumbnails (product_id, image_path) "
					+ "VALUES (%d, '%s');\n", i, imagePath));
		}

		log.warn(sql.toString());
	}

}
