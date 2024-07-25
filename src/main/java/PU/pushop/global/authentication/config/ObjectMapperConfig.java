package PU.pushop.global.authentication.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Configuration class for customizing the ObjectMapper used for object serialization and deserialization.
 * 이 클래스는 Spring IoC 컨테이너를 통해 ObjectMapper를 글로벌하게 설정하고, 애플리케이션 전체에서 사용하는 ObjectMapper의 동작 방식을 수정합니다.
 * 주로 JSON 변환 설정, 날짜/시간 타입 처리 등의 구성을 변경하는 데 사용됩니다.
 * "2024-07-12T20:09:18" 와 같이 ISO 8601 형식의 문자열로 직렬화 해줍니다.
 */
@Configuration
public class ObjectMapperConfig {

    @Bean
    public JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }

    @Primary
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.registerModule(javaTimeModule());
        return objectMapper;
    }
}