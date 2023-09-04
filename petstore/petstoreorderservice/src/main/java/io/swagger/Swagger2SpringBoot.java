package io.swagger;

import com.azure.spring.data.cosmos.Constants;
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;
import com.chtrembl.petstore.order.model.ContainerEnvironment;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableCaching
@EnableSwagger2
@EnableCosmosRepositories("com.chtrembl.petstore.order.control")
@EntityScan("com.chtrembl.petstore.order.entity")
@ComponentScan(basePackages = {"io.swagger", "com.chtrembl.petstore.order", "io.swagger.configuration"})
public class Swagger2SpringBoot {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public ContainerEnvironment containerEnvironment() {
        return new ContainerEnvironment();
    }

    public static void main(String[] args) {
        SpringApplication.run(Swagger2SpringBoot.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        var javaTimeModule = new JavaTimeModule();
        return JsonMapper.builder()
                .addModule(javaTimeModule)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
    }

    @Bean(name = Constants.OBJECT_MAPPER_BEAN_NAME)
    public ObjectMapper cosmosObjectMapper() {
        var javaTimeModule = new JavaTimeModule();
        return JsonMapper.builder()
                .addModule(javaTimeModule)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .build();
    }
}
