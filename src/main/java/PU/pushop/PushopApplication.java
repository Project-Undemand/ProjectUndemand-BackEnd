package PU.pushop;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PushopApplication {

	public static void main(String[] args) {
		SpringApplication.run(PushopApplication.class, args);
	}

	@Bean
	Hibernate6Module hibernate6Module() {
		return new Hibernate6Module();
	}
}
