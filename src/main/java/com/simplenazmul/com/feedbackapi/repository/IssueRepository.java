package com.simplenazmul.com.feedbackapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.simplenazmul.com.feedbackapi.entity.Issue;

import java.util.Optional;
import java.util.Set;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    @Modifying
    @Query(value = "UPDATE issues SET issue_message = ?1 WHERE issue_id = ?2", nativeQuery = true)
    void updateMessageById(String issueMessage, Long issueId);

    @Modifying
    @Query(value = "UPDATE issues SET issue_state = ?1 WHERE issue_id = ?2", nativeQuery = true)
    void updateStateById(String issueState, Long issueId);

    @Query(value="SELECT issue_state FROM issues WHERE issue_id = ?1", nativeQuery = true)
    String findStateById(Long issueId);

    @Query(value="SELECT * FROM issues WHERE issue_reporter_account_id = ?1 AND issue_type_id = ?2 ORDER BY issue_id DESC LIMIT 1", nativeQuery=true)
    Optional<Issue> findByIssueReporterAccountIdAndIssueTypeId(Long accountRdbmsId, Short issueTypeRdbmsId);

    @Query(value="SELECT * FROM issues WHERE issue_type_id = ?1 ORDER BY issue_id DESC", nativeQuery=true)
    Page<Issue> findAllByIssueTypeId(Short issueTypeId, Pageable pageable);

    @Query(value="SELECT * FROM issues WHERE issue_type_id = ?1 AND issue_state = ?2 ORDER BY issue_id DESC", nativeQuery=true)
    Page<Issue> findAllByIssueTypeIdAndState(Short issueTypeId, String issueState, Pageable pageable);

    @Query(value="SELECT * FROM issues WHERE issue_reporter_account_id = ?1 ORDER BY issue_id DESC", nativeQuery=true)
    Page<Issue> findAllByIssueReporterAccountId(Long issueReporterAccountId, Pageable pageable);

    @Query(value="SELECT * FROM issues WHERE issue_reporter_account_id = ?1 AND issue_state = ?2 ORDER BY issue_id DESC", nativeQuery=true)
    Page<Issue> findAllByIssueReporterAccountIdAndState(Long issueReporterAccountId, String issueState, Pageable pageable);

    @Query(value="SELECT * FROM issues WHERE issue_state = ?1 ORDER BY issue_id DESC", nativeQuery=true)
    Page<Issue> findAllByState(String issueState, Pageable pageable);

    @Query(value="SELECT * FROM issues WHERE issue_message LIKE CONCAT('%',?1,'%') AND issue_state='Active' ORDER BY issue_id DESC", nativeQuery=true)
    Page<Issue> findAllByKeyword(String keyword, Pageable pageable);

    @Query(value="SELECT * FROM issues WHERE issue_id IN (?1) ORDER BY issue_id DESC", nativeQuery=true)
    Page<Issue> findAllByMultipleIds(Set<Short> multipleIds, Pageable pageable);

    @Query(value="SELECT count(issue_id) FROM issues WHERE issue_type_id = ?1", nativeQuery=true)
    Long countTotalByIssueTypeId(Short issueTypeId);

    @Query(value="SELECT count(issue_id) FROM issues WHERE issue_type_id = ?1 AND issue_state = ?2", nativeQuery=true)
    Long countTotalByIssueTypeIdAndState(Short issueTypeId, String issueState);

    @Query(value="SELECT count(issue_id) FROM issues WHERE issue_reporter_account_id = ?1", nativeQuery=true)
    Long countTotalByIssueReporterAccountId(Long issueReporterAccountId);

    @Query(value="SELECT count(issue_id) FROM issues WHERE issue_reporter_account_id = ?1 AND issue_state = ?2", nativeQuery=true)
    Long countTotalByIssueReporterAccountIdAndState(Long issueReporterAccountId, String issueState);

    @Query(value="SELECT count(issue_id) FROM issues WHERE issue_state = ?1", nativeQuery=true)
    Long countTotalByState(String issueState);

}