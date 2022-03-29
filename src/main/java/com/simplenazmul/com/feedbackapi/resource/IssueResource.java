package com.simplenazmul.com.feedbackapi.resource;

import java.sql.Timestamp;
import java.util.Objects;

import com.simplenazmul.com.feedbackapi.entity.IssueType;

public class IssueResource {

    private Long issueRdbmsId;

    private IssueType issueType;

    private AccountResource issueReporter;

    private String issueMessage;

    private String issueState;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    public IssueResource() {
    }

    public IssueResource(Long issueRdbmsId, IssueType issueType, AccountResource issueReporter, String issueMessage, String issueState, Timestamp createdAt, Timestamp updatedAt) {
        this.issueRdbmsId = issueRdbmsId;
        this.issueType = issueType;
        this.issueReporter = issueReporter;
        this.issueMessage = issueMessage;
        this.issueState = issueState;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getIssueRdbmsId() {
        return issueRdbmsId;
    }

    public void setIssueRdbmsId(Long issueRdbmsId) {
        this.issueRdbmsId = issueRdbmsId;
    }

    public IssueType getIssueType() {
        return issueType;
    }

    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
    }

    public AccountResource getIssueReporter() {
        return issueReporter;
    }

    public void setIssueReporter(AccountResource issueReporter) {
        this.issueReporter = issueReporter;
    }

    public String getIssueMessage() {
        return issueMessage;
    }

    public void setIssueMessage(String issueMessage) {
        this.issueMessage = issueMessage;
    }

    public String getIssueState() {
        return issueState;
    }

    public void setIssueState(String issueState) {
        this.issueState = issueState;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IssueResource that = (IssueResource) o;
        return Objects.equals(issueRdbmsId, that.issueRdbmsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issueRdbmsId);
    }

    @Override
    public String toString() {
        return "IssueResource{" +
                "issueRdbmsId=" + issueRdbmsId +
                ", issueType=" + issueType +
                ", issueReporter=" + issueReporter +
                ", issueMessage='" + issueMessage + '\'' +
                ", issueTypeState='" + issueState + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}