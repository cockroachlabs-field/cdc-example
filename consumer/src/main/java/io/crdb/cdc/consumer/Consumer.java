package io.crdb.cdc.consumer;

import io.crdb.cdc.common.ChangeFeed;
import io.crdb.cdc.common.DataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
public class Consumer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String UPSERT = "UPSERT INTO destination_table (id, balance, created_timestamp) VALUES (?, ?, ?)";
    private static final String DELETE = "DELETE FROM destination_table WHERE id = ?";

    private final DataSource dataSource;

    @Autowired
    public Consumer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @KafkaListener(topics = "source_table")
    public void listen(@Payload ChangeFeed changeFeed, @Headers MessageHeaders headers) {

        // kafka_receivedMessageKey
        // ["e8dd2ecf-b961-4df3-9b4c-4b6c2a07bffd"]

        log.debug("id = {}, value = {}", headers.getId(), changeFeed.toString());

        DataTable after = changeFeed.getAfter();

        if (after != null) {

            try (final Connection connection = dataSource.getConnection();
                 final PreparedStatement statement = connection.prepareStatement(UPSERT)) {

                statement.setObject(1, after.getId());
                statement.setInt(2, after.getBalance());
                statement.setTimestamp(3, after.getCreatedTimestamp());

                statement.executeUpdate();

                log.debug("upsert data into destination:  {}", after.toString());

            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }

        } else {

            try (final Connection connection = dataSource.getConnection();
                 final PreparedStatement statement = connection.prepareStatement(DELETE)) {

                statement.setObject(1, headers.getId());
                statement.executeUpdate();

                log.debug("deleted data from destination with id {}", headers.getId());

            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }


    }
}
