package com.simplenazmul.com.feedbackapi.service;

import org.springframework.data.domain.Pageable;

import com.simplenazmul.com.feedbackapi.entity.Issue;
import com.simplenazmul.com.feedbackapi.helper.Response;
import com.simplenazmul.com.feedbackapi.resource.IssueResource;

import java.util.Set;

public interface IssueService {

    Issue saveIssue(Short issueTypeRdbmsId, Long issueReporterAccountRdbmsId, String issueMessage);

    void updateIssueMessageByRdbmsId(String issueMessage, Long issueRdbmsId);
    void updateIssueStateByRdbmsId(String issueState, Long issueRdbmsId);

    void deleteIssueByRdbmsId(Long issueRdbmsId);

    IssueResource findIssueByRdbmsId(Long issueRdbmsId, String authorization);
    String findIssueStateByRdbmsId(Long issueRdbmsId);
    Response findAllIssues(Pageable pageable, String authorization);
    Response findAllIssuesByIssueTypeRdbmsId(Short issueTypeRdbmsId, Pageable pageable, String authorization);
    Response findAllIssuesByIssueTypeRdbmsIdAndState(Short issueTypeRdbmsId, String issueState, Pageable pageable, String authorization);
    Response findAllIssuesByIssueReporterAccountRdbmsId(Long issueReporterAccountRdbmsId, Pageable pageable, String authorization);
    Response findAllIssuesByIssueReporterAccountRdbmsIdAndState(Long issueReporterAccountRdbmsId, String issueState, Pageable pageable, String authorization);
    Response findAllIssuesByState(String issueState, Pageable pageable, String authorization);
    Response findAllIssuesByKeyword(String keyword, Pageable pageable, String authorization);
    Response findAllIssuesByMultipleRdbmsIds(Set<Short> multipleRdbmsIds, Pageable pageable, String authorization);

    Boolean exists(Long issueRdbmsId);
    Boolean exists(Long accountRdbmsId, Short issueTypeRdbmsId, String issueMessage);
    Boolean issueTypeExists(Short issueTypeRdbmsId);
    Boolean accountExists(Long accountRdbmsId, String authorization);

    Long countTotalIssues();
    Long countTotalIssuesByIssueTypeRdbmsId(Short issueTypeRdbmsId);
    Long countTotalIssuesByIssueTypeRdbmsIdAndState(Short issueTypeRdbmsId, String issueState);
    Long countTotalIssuesByIssueReporterAccountRdbmsId(Long issueReporterAccountRdbmsId);
    Long countTotalIssuesByIssueReporterAccountRdbmsIdAndState(Long issueReporterAccountRdbmsId, String issueState);
    Long countTotalIssuesByState(String issueState);

}