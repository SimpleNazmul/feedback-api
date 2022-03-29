package com.simplenazmul.com.feedbackapi.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @Column(name = "issue_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issueRdbmsId;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "issue_type_id", nullable = false)
    private IssueType issueType;

    @NotNull
    @Column(name = "issue_reporter_account_id", nullable = false)
    private Long issueReporterAccountRdbmsId;

    @NotNull
    @NotBlank
    @Size(min = 5, max = 500)
    @Column(name = "issue_message", nullable = false)
    private String issueMessage;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 10)
    @Column(name = "issue_state", nullable = false)
    private String issueTypeState;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    public Issue() {
    }

    public Issue(Long issueRdbmsId, IssueType issueType, Long issueReporterAccountRdbmsId, String issueMessage, String issueTypeState, Timestamp createdAt, Timestamp updatedAt) {
        this.issueRdbmsId = issueRdbmsId;
        this.issueType = issueType;
        this.issueReporterAccountRdbmsId = issueReporterAccountRdbmsId;
        this.issueMessage = issueMessage;
        this.issueTypeState = issueTypeState;
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

    public Long getIssueReporterAccountRdbmsId() {
        return issueReporterAccountRdbmsId;
    }

    public void setIssueReporterAccountRdbmsId(Long issueReporterAccountRdbmsId) {
        this.issueReporterAccountRdbmsId = issueReporterAccountRdbmsId;
    }

    public String getIssueMessage() {
        return issueMessage;
    }

    public void setIssueMessage(String issueMessage) {
        this.issueMessage = issueMessage;
    }

    public String getIssueTypeState() {
        return issueTypeState;
    }

    public void setIssueTypeState(String issueTypeState) {
        this.issueTypeState = issueTypeState;
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
        Issue issue = (Issue) o;
        return issueRdbmsId.equals(issue.issueRdbmsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issueRdbmsId);
    }

    @Override
    public String toString() {
        return "Issue{" +
                "issueRdbmsId=" + issueRdbmsId +
                ", issueType=" + issueType +
                ", issueReporterAccountRdbmsId=" + issueReporterAccountRdbmsId +
                ", issueMessage='" + issueMessage + '\'' +
                ", issueTypeState='" + issueTypeState + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}