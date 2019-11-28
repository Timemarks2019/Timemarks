package com.asoft.timemarks.models;

public class ItemTransaction {
    private String id;
    private String tran_type;
    private String table_name;
    private String table_row_id;
    private String user_id;
    private String amount;
    private String remained_balance;
    private String remarks;
    private String admin_remarks;
    private String encoded_fields;
    private String balance_type;
    private String inserted_at;
    private String name;
    private String agent_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTran_type() {
        return tran_type;
    }

    public void setTran_type(String tran_type) {
        this.tran_type = tran_type;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getTable_row_id() {
        return table_row_id;
    }

    public void setTable_row_id(String table_row_id) {
        this.table_row_id = table_row_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRemained_balance() {
        return remained_balance;
    }

    public void setRemained_balance(String remained_balance) {
        this.remained_balance = remained_balance;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAdmin_remarks() {
        return admin_remarks;
    }

    public void setAdmin_remarks(String admin_remarks) {
        this.admin_remarks = admin_remarks;
    }

    public String getEncoded_fields() {
        return encoded_fields;
    }

    public void setEncoded_fields(String encoded_fields) {
        this.encoded_fields = encoded_fields;
    }

    public String getBalance_type() {
        return balance_type;
    }

    public void setBalance_type(String balance_type) {
        this.balance_type = balance_type;
    }

    public String getInserted_at() {
        return inserted_at;
    }

    public void setInserted_at(String inserted_at) {
        this.inserted_at = inserted_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }
}
