package io.crdb.cdc.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.management.timer.Timer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component
public class ConsumerTableInitializer implements CommandLineRunner {

    @Autowired
    private ResourceLoader resourceLoader;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        Resource resource = resourceLoader.getResource("classpath:schema.sql");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String sqlScript = reader.lines().collect(Collectors.joining("\n"));
            jdbcTemplate.execute(sqlScript);
            log.debug("executing sql statement ");
        } catch (IOException e) {
            throw new RuntimeException("Failed to read SQL script", e);
        }
    }
}
