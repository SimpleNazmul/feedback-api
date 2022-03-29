package com.simplenazmul.com.feedbackapi.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @Column(name = "feedback_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackRdbmsId;

    @NotNull
    @Column(name = "feedback_giver_account_id", nullable = false)
    private Long feedbackGiverAccountRdbmsId;

    @NotNull
    @NotBlank
    @Size(min = 5, max = 500)
    @Column(name = "feedback", nullable = false)
    private String feedback;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 10)
    @Column(name = "feedback_state", nullable = false)
    private String feedbackState;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    public Feedback() {
    }

    public Feedback(Long feedbackRdbmsId, Long feedbackGiverAccountRdbmsId, String feedback, String feedbackState, Timestamp createdAt, Timestamp updatedAt) {
        this.feedbackRdbmsId = feedbackRdbmsId;
        this.feedbackGiverAccountRdbmsId = feedbackGiverAccountRdbmsId;
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

    public Long getFeedbackGiverAccountRdbmsId() {
        return feedbackGiverAccountRdbmsId;
    }

    public void setFeedbackGiverAccountRdbmsId(Long feedbackGiverAccountRdbmsId) {
        this.feedbackGiverAccountRdbmsId = feedbackGiverAccountRdbmsId;
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
        Feedback feedback = (Feedback) o;
        return feedbackRdbmsId.equals(feedback.feedbackRdbmsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackRdbmsId);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "feedbackRdbmsId=" + feedbackRdbmsId +
                ", feedbackGiverAccountRdbmsId=" + feedbackGiverAccountRdbmsId +
                ", feedback='" + feedback + '\'' +
                ", feedbackState='" + feedbackState + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}