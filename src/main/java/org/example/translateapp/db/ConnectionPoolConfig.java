package org.example.translateapp.db;

import lombok.Data;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.sql.SQLException;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class ConnectionPoolConfig {

    private String url;
    private String username;
    private String password;

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ConnectionPool connectionPool() throws SQLException {
        return new ConnectionPool(url, username, password);
    }

}
