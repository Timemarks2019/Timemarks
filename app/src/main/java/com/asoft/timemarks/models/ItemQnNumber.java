package com.asoft.timemarks.models;

public class ItemQnNumber {
    private String qnNumber;
    private boolean isAttempted;

    public ItemQnNumber(String qnNumber, boolean isAttempted){
        this.qnNumber = qnNumber;
        this.isAttempted = isAttempted;
    }
    public String getQnNumber() {
        return qnNumber;
    }

    public void setQnNumber(String id) {
        this.qnNumber = qnNumber;
    }

    public boolean isAttempted() {
        return isAttempted;
    }

    public void setAttempted(boolean isAttempted) {
        this.isAttempted = isAttempted;
    }
}
