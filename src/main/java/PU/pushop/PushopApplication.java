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

}
