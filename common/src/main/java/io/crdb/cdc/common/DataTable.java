package io.crdb.cdc.common;

import java.sql.Timestamp;
import java.util.UUID;

public class DataTable {

    private UUID id;

    private Integer balance;

    private Timestamp createdTimestamp;

    public DataTable(UUID id, Integer balance, Timestamp createdTimestamp) {
        this.id = id;
        this.balance = balance;
        this.createdTimestamp = createdTimestamp;
    }

    public UUID getId() {
        return id;
    }

    public Integer getBalance() {
        return balance;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    @Override
    public String toString() {
        return "DataTable{" +
                "id=" + id +
                ", balance=" + balance +
                ", createdTimestamp=" + createdTimestamp +
                '}';
    }
}
