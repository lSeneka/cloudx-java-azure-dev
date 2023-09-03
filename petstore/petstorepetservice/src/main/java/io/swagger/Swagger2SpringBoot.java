package io.swagger;

import com.chtrembl.petstore.pet.model.ContainerEnvironment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableJpaRepositories("com.chtrembl.petstore.pet.control")
@EntityScan("com.chtrembl.petstore.pet.entity")
@ComponentScan(basePackages = {"io.swagger", "com.chtrembl.petstore.pet.api", "io.swagger.configuration", "com.chtrembl.petstore.pet.control"})
public class Swagger2SpringBoot {

    @Bean
    public ContainerEnvironment containerEnvironment() {
        return new ContainerEnvironment();
    }

    public static void main(String[] args) {
        SpringApplication.run(Swagger2SpringBoot.class, args);
    }
}
