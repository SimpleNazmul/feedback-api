package com.simplenazmul.com.feedbackapi.resource;

import java.sql.Timestamp;
import java.util.Objects;

public class FeedbackResource {

    private Long feedbackRdbmsId;

    private AccountResource feedbackGiver;

    private String feedback;

    private String feedbackState;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    public FeedbackResource() {
    }

    public FeedbackResource(Long feedbackRdbmsId, AccountResource feedbackGiver, String feedback, String feedbackState, Timestamp createdAt, Timestamp updatedAt) {
        this.feedbackRdbmsId = feedbackRdbmsId;
        this.feedbackGiver = feedbackGiver;
        this.feedback = feedback;
        this.feedbackState = feedbackState;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getFeedbackRdbmsId() {
        return feedbackRdbmsId;
    }

    public void setFeedbackRdbmsId(Long feedbackRdbmsId) {
        this.feedbackRdbmsId = feedbackRdbmsId;
    }

    public AccountResource getFeedbackGiver() {
        return feedbackGiver;
    }

    public void setFeedbackGiver(AccountResource feedbackGiver) {
        this.feedbackGiver = feedbackGiver;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFeedbackState() {
        return feedbackState;
    }

    public void setFeedbackState(String feedbackState) {
        this.feedbackState = feedbackState;
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
        FeedbackResource that = (FeedbackResource) o;
        return Objects.equals(feedbackRdbmsId, that.feedbackRdbmsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackRdbmsId);
    }

    @Override
    public String toString() {
        return "FeedbackResource{" +
                "feedbackRdbmsId=" + feedbackRdbmsId +
                ", feedbackGiver=" + feedbackGiver +
                ", feedback='" + feedback + '\'' +
                ", feedbackState='" + feedbackState + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}