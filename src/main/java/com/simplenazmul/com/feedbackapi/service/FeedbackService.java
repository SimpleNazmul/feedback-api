package com.simplenazmul.com.feedbackapi.service;

import org.springframework.data.domain.Pageable;

import com.simplenazmul.com.feedbackapi.entity.Feedback;
import com.simplenazmul.com.feedbackapi.helper.Response;
import com.simplenazmul.com.feedbackapi.resource.FeedbackResource;

import java.util.Set;

public interface FeedbackService {

    Feedback saveFeedback(Long feedbackGiverAccountRdbmsId, String feedback);

    void updateFeedbackByRdbmsId(String feedback, Long feedbackRdbmsId);
    void updateFeedbackStateByRdbmsId(String feedbackState, Long feedbackRdbmsId);

    void deleteFeedbackByRdbmsId(Long feedbackRdbmsId);

    FeedbackResource findFeedbackByRdbmsId(Long feedbackRdbmsId, String authorization);
    String findFeedbackStateByRdbmsId(Long feedbackRdbmsId);
    Response findAllFeedbacks(Pageable pageable, String authorization);
    Response findAllFeedbacksByFeedbackGiverAccountRdbmsId(Long feedbackGiverAccountRdbmsId, Pageable pageable, String authorization);
    Response findAllFeedbacksByFeedbackGiverAccountRdbmsIdAndState(Long feedbackGiverAccountRdbmsId, String feedbackState, Pageable pageable, String authorization);
    Response findAllFeedbacksByState(String feedbackState, Pageable pageable, String authorization);
    Response findAllFeedbacksByKeyword(String keyword, Pageable pageable, String authorization);
    Response findAllFeedbacksByMultipleRdbmsIds(Set<Short> multipleRdbmsIds, Pageable pageable, String authorization);
    Response findAllFeedbacksOfTheDay(String dateString, Pageable pageable, String authorization);
    Response findAllFeedbacksOfTheWeek(String dateString, Pageable pageable, String authorization);
    Response findAllFeedbacksOfTheMonth(String dateString, Pageable pageable, String authorization);
    Response findAllFeedbacksOfTheYear(String dateString, Pageable pageable, String authorization);

    Boolean exists(Long feedbackRdbmsId);
    Boolean exists(Long accountRdbmsId, String feedback);
    Boolean accountExists(Long accountRdbmsId, String authorization);

    Long countTotalFeedbacks();
    Long countTotalFeedbacksOfTheDay(String dateString);
    Long countTotalFeedbacksOfTheWeek(String dateString);
    Long countTotalFeedbacksOfTheMonth(String dateString);
    Long countTotalFeedbacksOfTheYear(String dateString);
    Long countTotalFeedbacksByFeedbackGiverAccountRdbmsId(Long feedbackGiverAccountRdbmsId);
    Long countTotalFeedbacksByFeedbackGiverAccountRdbmsIdAndState(Long feedbackGiverAccountRdbmsId, String feedbackState);
    Long countTotalFeedbacksByState(String feedbackState);

}