package io.crdb.cdc.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.management.timer.Timer;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

@Component
public class DataGenerator implements ApplicationRunner {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String SQL = "INSERT INTO source_table (id, balance) VALUES (?, ?)";

    private DataSource dataSource;

    @Autowired
    public DataGenerator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        for (int i = 0; i < 10; i++) {

            try (final Connection connection = dataSource.getConnection();
                 final PreparedStatement statement = connection.prepareStatement(SQL)) {

                statement.setObject(1, UUID.randomUUID());
                statement.setInt(2, i);

                final int updated = statement.executeUpdate();

                log.debug("updated {} records for iteration {}", updated, i);

                Thread.sleep(Timer.ONE_SECOND);
            }
        }

    }
}
