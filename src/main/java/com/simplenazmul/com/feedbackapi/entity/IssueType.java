package com.simplenazmul.com.feedbackapi.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "issue_types")
public class IssueType {

    @Id
    @Column(name = "issue_type_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short issueTypeRdbmsId;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "issue_type_name", nullable = false, unique = true)
    private String issueTypeName;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "issue_type_url_name", nullable = false, unique = true)
    private String issueTypeUrlName;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 10)
    @Column(name = "issue_type_state", nullable = false)
    private String issueTypeState;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    public IssueType() {
    }

    public IssueType(Short issueTypeRdbmsId, String issueTypeName, String issueTypeUrlName, String issueTypeState, Timestamp createdAt, Timestamp updatedAt) {
        this.issueTypeRdbmsId = issueTypeRdbmsId;
        this.issueTypeName = issueTypeName;
        this.issueTypeUrlName = issueTypeUrlName;
        this.issueTypeState = issueTypeState;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Short getIssueTypeRdbmsId() {
        return issueTypeRdbmsId;
    }

    public void setIssueTypeRdbmsId(Short issueTypeRdbmsId) {
        this.issueTypeRdbmsId = issueTypeRdbmsId;
    }

    public String getIssueTypeName() {
        return issueTypeName;
    }

    public void setIssueTypeName(String issueTypeName) {
        this.issueTypeName = issueTypeName;
    }

    public String getIssueTypeUrlName() {
        return issueTypeUrlName;
    }

    public void setIssueTypeUrlName(String issueTypeUrlName) {
        this.issueTypeUrlName = issueTypeUrlName;
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
        IssueType issueType = (IssueType) o;
        return issueTypeRdbmsId.equals(issueType.issueTypeRdbmsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issueTypeRdbmsId);
    }

    @Override
    public String toString() {
        return "IssueType{" +
                "issueTypeRdbmsId=" + issueTypeRdbmsId +
                ", issueTypeName='" + issueTypeName + '\'' +
                ", issueTypeUrlName='" + issueTypeUrlName + '\'' +
                ", issueTypeState='" + issueTypeState + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}