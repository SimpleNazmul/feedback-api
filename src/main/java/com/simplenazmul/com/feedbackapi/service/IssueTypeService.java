package com.simplenazmul.com.feedbackapi.service;

import org.springframework.data.domain.Pageable;

import com.simplenazmul.com.feedbackapi.entity.IssueType;
import com.simplenazmul.com.feedbackapi.helper.Response;

import java.util.Set;

public interface IssueTypeService {

    IssueType saveIssueType(String issueTypeName, String issueTypeUrlName);

    void updateIssueTypeNameByRdbmsId(String issueTypeName, Short issueTypeRdbmsId);
    void updateIssueTypeUrlNameByRdbmsId(String issueTypeUrlName, Short issueTypeRdbmsId);
    void updateIssueTypeStateByRdbmsId(String issueTypeState, Short issueTypeRdbmsId);

    void deleteIssueTypeByRdbmsId(Short issueTypeRdbmsId);

    IssueType findIssueTypeByRdbmsId(Short issueTypeRdbmsId);
    IssueType findIssueTypeByName(String issueTypeName);
    IssueType findIssueTypeByUrlName(String issueTypeUrlName);
    String findIssueTypeStateByRdbmsId(Short issueTypeRdbmsId);
    Response findAllIssueTypes(Pageable pageable);
    Response findAllIssueTypesByState(String issueTypeState, Pageable pageable);
    Response findAllIssueTypesByKeyword(String keyword, Pageable pageable);
    Response findAllIssueTypesByMultipleRdbmsIds(Set<Short> multipleRdbmsIds, Pageable pageable);

    Boolean exists(Short issueTypeRdbmsId);
    Boolean nameExists(String issueTypeName);
    Boolean urlNameExists(String issueTypeUrlName);

    Long countTotalIssueTypes();
    Short countTotalIssueTypesByState(String issueTypeState);
    
}