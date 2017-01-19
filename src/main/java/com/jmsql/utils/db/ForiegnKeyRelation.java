package com.jmsql.utils.db;

public class ForiegnKeyRelation {

    private String referrerTable;
    private String referrerColumn;
    private String primaryTable;
    private String primaryColumn;

    public ForiegnKeyRelation(String referrerTable, String referrerColumn, String primaryTable, String primaryColumn) {
        super();
        this.referrerTable = referrerTable;
        this.referrerColumn = referrerColumn;
        this.primaryTable = primaryTable;
        this.primaryColumn = primaryColumn;
    }

    public String getReferrerTable() {
        return referrerTable;
    }

    public void setReferrerTable(String referrerTable) {
        this.referrerTable = referrerTable;
    }

    public String getReferrerColumn() {
        return referrerColumn;
    }

    public void setReferrerColumn(String referrerColumn) {
        this.referrerColumn = referrerColumn;
    }

    public String getPrimaryTable() {
        return primaryTable;
    }

    public void setPrimaryTable(String primaryTable) {
        this.primaryTable = primaryTable;
    }

    public String getPrimaryColumn() {
        return primaryColumn;
    }

    public void setPrimaryColumn(String primaryColumn) {
        this.primaryColumn = primaryColumn;
    }

}
