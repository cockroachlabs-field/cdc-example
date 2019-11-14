package com.cockroach.cdc;

public class KV {

    private String k;

    private String v;

    public KV() {
    }

    public KV(String k, String v) {
        this.k = k;
        this.v = v;
    }

    public String getK() {
        return k;
    }

    public String getV() {
        return v;
    }

    @Override
    public String toString() {
        return "KV{" +
                "k='" + k + '\'' +
                ", v='" + v + '\'' +
                '}';
    }
}
