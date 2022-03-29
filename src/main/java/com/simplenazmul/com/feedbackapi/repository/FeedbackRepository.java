package com.simplenazmul.com.feedbackapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.simplenazmul.com.feedbackapi.entity.Feedback;

import java.util.Optional;
import java.util.Set;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Modifying
    @Query(value = "UPDATE feedbacks SET feedback = ?1 WHERE feedback_id = ?2", nativeQuery = true)
    void updateFeedbackById(String feedback, Long feedbackId);

    @Modifying
    @Query(value = "UPDATE feedbacks SET feedback_state = ?1 WHERE feedback_id = ?2", nativeQuery = true)
    void updateStateById(String feedbackState, Long feedbackId);

    @Query(value="SELECT feedback_state FROM feedbacks WHERE feedback_id = ?1", nativeQuery = true)
    String findStateById(Long feedbackId);

    @Query(value="SELECT * FROM feedbacks WHERE feedback_giver_account_id = ?1 ORDER BY feedback_id DESC LIMIT 1", nativeQuery=true)
    Optional<Feedback> findByFeedbackGiverAccountId(Long accountRdbmsId);

    @Query(value="SELECT * FROM feedbacks ORDER BY feedback_id DESC", nativeQuery=true)
    Page<Feedback> findAllFeedbacks(Pageable pageable);

    @Query(value="SELECT * FROM feedbacks WHERE feedback_giver_account_id = ?1 ORDER BY feedback_id DESC", nativeQuery=true)
    Page<Feedback> findAllByFeedbackGiverAccountId(Long feedbackGiverAccountId, Pageable pageable);

    @Query(value="SELECT * FROM feedbacks WHERE feedback_giver_account_id = ?1 AND feedback_state = ?2 ORDER BY feedback_id DESC", nativeQuery=true)
    Page<Feedback> findAllByFeedbackGiverAccountIdAndState(Long feedbackGiverAccountId, String feedbackState, Pageable pageable);

    @Query(value="SELECT * FROM feedbacks WHERE feedback_state = ?1 ORDER BY feedback_id DESC", nativeQuery=true)
    Page<Feedback> findAllByState(String feedbackState, Pageable pageable);

    @Query(value="SELECT * FROM feedbacks WHERE feedback LIKE CONCAT('%',?1,'%') AND feedback_state='Active' ORDER BY feedback_id DESC", nativeQuery=true)
    Page<Feedback> findAllByKeyword(String keyword, Pageable pageable);

    @Query(value="SELECT * FROM feedbacks WHERE feedback_id IN (?1) ORDER BY feedback_id DESC", nativeQuery=true)
    Page<Feedback> findAllByMultipleIds(Set<Short> multipleIds, Pageable pageable);

    @Query(value="SELECT * FROM feedbacks WHERE DATE(created_at) = DATE(?1) ORDER BY feedback_id DESC", nativeQuery=true)
    Page<Feedback> findAllOfTheDay(String dateString, Pageable pageable);

    @Query(value="SELECT * FROM feedbacks WHERE WEEK(created_at) = WEEK(?1) ORDER BY feedback_id DESC", nativeQuery=true)
    Page<Feedback> findAllOfTheWeek(String dateString, Pageable pageable);

    @Query(value="SELECT * FROM feedbacks WHERE MONTH(created_at) = MONTH(?1) ORDER BY feedback_id DESC", nativeQuery=true)
    Page<Feedback> findAllOfTheMonth(String dateString, Pageable pageable);

    @Query(value="SELECT * FROM feedbacks WHERE YEAR(created_at) = YEAR(?1) ORDER BY feedback_id DESC", nativeQuery=true)
    Page<Feedback> findAllOfTheYear(String dateString, Pageable pageable);

    @Query(value="SELECT count(feedback_id) FROM feedbacks WHERE feedback_giver_account_id = ?1", nativeQuery=true)
    Long countTotalByFeedbackGiverAccountId(Long feedbackGiverAccountId);

    @Query(value="SELECT count(feedback_id) FROM feedbacks WHERE feedback_giver_account_id = ?1 AND feedback_state = ?2", nativeQuery=true)
    Long countTotalByFeedbackGiverAccountIdAndState(Long feedbackGiverAccountId, String feedbackState);

    @Query(value="SELECT count(feedback_id) FROM feedbacks WHERE feedback_state = ?1", nativeQuery=true)
    Long countTotalByState(String feedbackState);

    @Query(value="SELECT count(feedback_id) FROM feedbacks WHERE DATE(created_at) = DATE(?1)", nativeQuery=true)
    Long countTotalOfTheDay(String dateString);

    @Query(value="SELECT count(feedback_id) FROM feedbacks WHERE WEEK(created_at) = WEEK(?1)", nativeQuery=true)
    Long countTotalOfTheWeek(String dateString);

    @Query(value="SELECT count(feedback_id) FROM feedbacks WHERE MONTH(created_at) = MONTH(?1)", nativeQuery=true)
    Long countTotalOfTheMonth(String dateString);

    @Query(value="SELECT count(feedback_id) FROM feedbacks WHERE YEAR(created_at) = YEAR(?1)", nativeQuery=true)
    Long countTotalOfTheYear(String dateString);

}