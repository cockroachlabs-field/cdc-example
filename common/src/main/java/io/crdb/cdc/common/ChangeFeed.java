package io.crdb.cdc.common;

public class ChangeFeed {

    private DataTable after;

    private String updated;

    private String resolved;

    public ChangeFeed() {
    }

    public DataTable getAfter() {
        return after;
    }

    public void setAfter(DataTable after) {
        this.after = after;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getResolved() {
        return resolved;
    }

    public void setResolved(String resolved) {
        this.resolved = resolved;
    }

    @Override
    public String toString() {
        return "ChangeFeed{" +
                "after=" + after +
                ", updated='" + updated + '\'' +
                ", resolved='" + resolved + '\'' +
                '}';
    }
}
