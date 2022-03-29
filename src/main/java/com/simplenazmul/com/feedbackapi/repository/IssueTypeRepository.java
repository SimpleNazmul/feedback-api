package com.simplenazmul.com.feedbackapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.simplenazmul.com.feedbackapi.entity.IssueType;

import java.util.Optional;
import java.util.Set;

@Repository
public interface IssueTypeRepository extends JpaRepository<IssueType, Short> {

    @Modifying
    @Query(value = "UPDATE issue_types SET issue_type_name = ?1 WHERE issue_type_id = ?2", nativeQuery = true)
    void updateNameById(String issueTypeName, Short issueTypeId);

    @Modifying
    @Query(value = "UPDATE issue_types SET issue_type_url_name = ?1 WHERE issue_type_id = ?2", nativeQuery = true)
    void updateUrlNameById(String issueTypeUrlName, Short issueTypeId);

    @Modifying
    @Query(value = "UPDATE issue_types SET issue_type_state = ?1 WHERE issue_type_id = ?2", nativeQuery = true)
    void updateStateById(String issueTypeState, Short issueTypeId);

    @Query(value="SELECT issue_type_state FROM issue_types WHERE issue_type_id = ?1", nativeQuery = true)
    String findStateById(Short issueTypeId);

    @Query(value="SELECT * FROM issue_types WHERE issue_type_name = ?1", nativeQuery=true)
    Optional<IssueType> findByName(String issueTypeName);

    @Query(value="SELECT * FROM issue_types WHERE issue_type_url_name=?1", nativeQuery=true)
    Optional<IssueType> findByUrlName(String issueTypeUrlName);

    @Query(value="SELECT * FROM issue_types WHERE issue_type_state = ?1 ORDER BY issue_type_id DESC", nativeQuery=true)
    Page<IssueType> findAllByState(String issueTypeState, Pageable pageable);

    @Query(value="SELECT * FROM issue_types WHERE issue_type_name LIKE CONCAT('%',?1,'%') AND issue_type_state='Active' ORDER BY issue_type_id DESC", nativeQuery=true)
    Page<IssueType> findAllByKeyword(String keyword, Pageable pageable);

    @Query(value="SELECT * FROM issue_types WHERE issue_type_id IN (?1) ORDER BY issue_type_id DESC", nativeQuery=true)
    Page<IssueType> findAllByMultipleIds(Set<Short> multipleIds, Pageable pageable);

    @Query(value="SELECT count(issue_type_id) FROM issue_types WHERE issue_type_state = ?1", nativeQuery=true)
    Short countTotalByState(String issueTypeState);
    
}