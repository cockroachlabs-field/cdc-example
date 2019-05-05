package io.crdb.cdc.common;

import java.util.UUID;

public class DataTable {

    private UUID id;

    private Integer balance;

    public DataTable(UUID id, Integer balance) {
        this.id = id;
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public Integer getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "DataTable{" +
                "id=" + id +
                ", balance=" + balance +
                '}';
    }
}
