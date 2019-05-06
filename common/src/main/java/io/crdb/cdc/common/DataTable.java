package io.crdb.cdc.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.UUID;

public class DataTable {

    private UUID id;

    private Integer balance;

    @JsonProperty("created_timestamp")
    private Timestamp createdTimestamp;

    public DataTable() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
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
