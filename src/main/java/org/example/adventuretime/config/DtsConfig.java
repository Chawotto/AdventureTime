package org.example.adventuretime.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DtsConfig {

    @Value("${SPRING_DATASOURCE_URL:}")
    private String springDatasourceUrl;

    @Value("${MYSQL_URL:}")
    private String mysqlUrl;

    @PostConstruct
    public void fixDatasourceUrl() {
        if ((springDatasourceUrl == null || springDatasourceUrl.isBlank()) && mysqlUrl.startsWith("mysql://")) {
            String correctedUrl = "jdbc:" + mysqlUrl;
            System.setProperty("SPRING_DATASOURCE_URL", correctedUrl);
            System.out.println("Datasource URL corrected: " + correctedUrl);
        }
    }
}
