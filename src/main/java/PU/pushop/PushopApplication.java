package PU.pushop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
//@ComponentScan(basePackages = {"PU.pushop", "PU.pushop.global.authentication.config"})
public class PushopApplication {

	public static void main(String[] args) {
		SpringApplication.run(PushopApplication.class, args);
	}

}
