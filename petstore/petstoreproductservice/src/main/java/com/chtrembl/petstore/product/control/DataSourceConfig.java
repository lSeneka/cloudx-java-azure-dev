package com.chtrembl.petstore.product.control;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Setter;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.primary")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource primaryDataSource(DataSourceProperties primaryDataSourceProperties) {
        return createDataSource(primaryDataSourceProperties);
    }

    @Bean
    @ConfigurationProperties("spring.datasource.liquibase")
    public DataSourceProperties liquibaseDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @LiquibaseDataSource
    public DataSource liquibaseDataSource(DataSourceProperties liquibaseDataSourceProperties) {
        return createDataSource(liquibaseDataSourceProperties);
    }

    private static DataSource createDataSource(DataSourceProperties properties) {
        var hds = DataSourceBuilder.create()
                .url(properties.url)
                .username(properties.username)
                .password(properties.password)
                .type(HikariDataSource.class)
                .build();
        hds.setPoolName(properties.hikari.poolName);
        hds.setMaximumPoolSize(properties.hikari.maxPoolSize);
        return hds;
    }

    @Setter
    public static class DataSourceProperties {

        @Setter
        static class Hikari {
            private String poolName;
            private Integer maxPoolSize;
        }

        private String url;
        private String username;
        private String password;
        private Hikari hikari;
    }
}