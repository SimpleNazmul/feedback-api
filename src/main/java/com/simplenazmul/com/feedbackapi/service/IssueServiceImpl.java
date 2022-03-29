package com.simplenazmul.com.feedbackapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simplenazmul.com.feedbackapi.entity.Issue;
import com.simplenazmul.com.feedbackapi.exception.InternalServerErrorException;
import com.simplenazmul.com.feedbackapi.exception.NotFoundException;
import com.simplenazmul.com.feedbackapi.helper.Helper;
import com.simplenazmul.com.feedbackapi.helper.Response;
import com.simplenazmul.com.feedbackapi.helper.State;
import com.simplenazmul.com.feedbackapi.repository.IssueRepository;
import com.simplenazmul.com.feedbackapi.resource.AccountResource;
import com.simplenazmul.com.feedbackapi.resource.IssueResource;

import java.sql.Timestamp;
import java.util.*;

@Service
@Transactional
public class IssueServiceImpl implements IssueService {

    Logger logger = LoggerFactory.getLogger(IssueServiceImpl.class);

    @Autowired
    private HelperService helperService;

    @Autowired
    private IssueTypeService issueTypeService;

    @Autowired
    private MySQLApiService mySQLApiService;

    @Autowired
    private IssueRepository issueRepository;

    @Override
    public Issue saveIssue(Short issueTypeRdbmsId, Long issueReporterAccountRdbmsId, String issueMessage) {

        Timestamp timestamp = Helper.getCurrentTimestamp();

        Issue issue = new Issue();
        issue.setIssueType(issueTypeService.findIssueTypeByRdbmsId(issueTypeRdbmsId));
        issue.setIssueReporterAccountRdbmsId(issueReporterAccountRdbmsId);
        issue.setIssueMessage(issueMessage);
        issue.setIssueTypeState(State.Active.name());
        issue.setCreatedAt(timestamp);
        issue.setUpdatedAt(timestamp);

        try {
            return issueRepository.save(issue);
        } catch (Exception e) {
            logger.error("Error in saving issue. ERROR: " + e.getMessage());
            throw new InternalServerErrorException("Something went wrong on the server!");
        }

    }

    @Override
    public void updateIssueMessageByRdbmsId(String issueMessage, Long issueRdbmsId) {

        try {
            issueRepository.updateMessageById(issueMessage, issueRdbmsId);
        } catch (Exception e) {
            logger.info("Error in updating issue message. ERROR: " + e.getMessage());
            throw new InternalServerErrorException("Something went wrong on the server!");
        }

    }

    @Override
    public void updateIssueStateByRdbmsId(String issueState, Long issueRdbmsId) {

        try {
            issueRepository.updateStateById(issueState, issueRdbmsId);
        } catch (Exception e) {
            logger.info("Error in updating issue state. ERROR: " + e.getMessage());
            throw new InternalServerErrorException("Something went wrong on the server!");
        }

    }

    @Override
    public void deleteIssueByRdbmsId(Long issueRdbmsId) {

        issueRepository.deleteById(issueRdbmsId);

    }

    @Override
    public IssueResource findIssueByRdbmsId(Long issueRdbmsId, String authorization) {

        Set<Long> accountRdbmsIds = new HashSet<>();
        Issue issue = issueRepository.findById(issueRdbmsId)
                .orElseThrow(() -> new NotFoundException("No issue with this issueRdbmsId is found!"));
        accountRdbmsIds.add(issue.getIssueReporterAccountRdbmsId());

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        return new IssueResource(
                issue.getIssueRdbmsId(),
                issue.getIssueType(),
                accountResources.get(0),
                issue.getIssueMessage(),
                issue.getIssueTypeState(),
                issue.getCreatedAt(),
                issue.getUpdatedAt()
        );

    }

    @Override
    public String findIssueStateByRdbmsId(Long issueRdbmsId) {
        return issueRepository.findStateById(issueRdbmsId);
    }

    @Override
    public Response findAllIssues(Pageable pageable, String authorization) {

        Set<Long> accountRdbmsIds = new HashSet<>();
        List<IssueResource> issueResources = new ArrayList<>();

        Page<Issue> issues = issueRepository.findAll(pageable);
        issues.forEach(issue -> accountRdbmsIds.add(issue.getIssueReporterAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        issues.forEach(issue -> accountResources.forEach(accountResource -> {
            if (issue.getIssueReporterAccountRdbmsId().equals(accountResource.getAccountId())) {
                issueResources.add(new IssueResource(
                        issue.getIssueRdbmsId(),
                        issue.getIssueType(),
                        accountResource,
                        issue.getIssueMessage(),
                        issue.getIssueTypeState(),
                        issue.getCreatedAt(),
                        issue.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(issues, issueResources, null);

    }

    @Override
    public Response findAllIssuesByIssueTypeRdbmsId(Short issueTypeRdbmsId, Pageable pageable, String authorization) {

        Set<Long> accountRdbmsIds = new HashSet<>();
        List<IssueResource> issueResources = new ArrayList<>();

        Page<Issue> issues = issueRepository.findAllByIssueTypeId(issueTypeRdbmsId, pageable);
        issues.forEach(issue -> accountRdbmsIds.add(issue.getIssueReporterAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        issues.forEach(issue -> accountResources.forEach(accountResource -> {
            if (issue.getIssueReporterAccountRdbmsId().equals(accountResource.getAccountId())) {
                issueResources.add(new IssueResource(
                        issue.getIssueRdbmsId(),
                        issue.getIssueType(),
                        accountResource,
                        issue.getIssueMessage(),
                        issue.getIssueTypeState(),
                        issue.getCreatedAt(),
                        issue.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(issues, issueResources, null);

    }

    @Override
    public Response findAllIssuesByIssueTypeRdbmsIdAndState(Short issueTypeRdbmsId, String issueState, Pageable pageable, String authorization) {

        Set<Long> accountRdbmsIds = new HashSet<>();
        List<IssueResource> issueResources = new ArrayList<>();

        Page<Issue> issues = issueRepository.findAllByIssueTypeIdAndState(issueTypeRdbmsId, issueState, pageable);
        issues.forEach(issue -> accountRdbmsIds.add(issue.getIssueReporterAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        issues.forEach(issue -> accountResources.forEach(accountResource -> {
            if (issue.getIssueReporterAccountRdbmsId().equals(accountResource.getAccountId())) {
                issueResources.add(new IssueResource(
                        issue.getIssueRdbmsId(),
                        issue.getIssueType(),
                        accountResource,
                        issue.getIssueMessage(),
                        issue.getIssueTypeState(),
                        issue.getCreatedAt(),
                        issue.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(issues, issueResources, null);

    }

    @Override
    public Response findAllIssuesByIssueReporterAccountRdbmsId(Long issueReporterAccountRdbmsId, Pageable pageable, String authorization) {

        Set<Long> accountRdbmsIds = new HashSet<>();
        List<IssueResource> issueResources = new ArrayList<>();

        Page<Issue> issues = issueRepository.findAllByIssueReporterAccountId(issueReporterAccountRdbmsId, pageable);
        issues.forEach(issue -> accountRdbmsIds.add(issue.getIssueReporterAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        issues.forEach(issue -> accountResources.forEach(accountResource -> {
            if (issue.getIssueReporterAccountRdbmsId().equals(accountResource.getAccountId())) {
                issueResources.add(new IssueResource(
                        issue.getIssueRdbmsId(),
                        issue.getIssueType(),
                        accountResource,
                        issue.getIssueMessage(),
                        issue.getIssueTypeState(),
                        issue.getCreatedAt(),
                        issue.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(issues, issueResources, null);

    }

    @Override
    public Response findAllIssuesByIssueReporterAccountRdbmsIdAndState(Long issueReporterAccountRdbmsId, String issueState, Pageable pageable, String authorization) {

        Set<Long> accountRdbmsIds = new HashSet<>();
        List<IssueResource> issueResources = new ArrayList<>();

        Page<Issue> issues = issueRepository.findAllByIssueReporterAccountIdAndState(issueReporterAccountRdbmsId, issueState, pageable);
        issues.forEach(issue -> accountRdbmsIds.add(issue.getIssueReporterAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        issues.forEach(issue -> accountResources.forEach(accountResource -> {
            if (issue.getIssueReporterAccountRdbmsId().equals(accountResource.getAccountId())) {
                issueResources.add(new IssueResource(
                        issue.getIssueRdbmsId(),
                        issue.getIssueType(),
                        accountResource,
                        issue.getIssueMessage(),
                        issue.getIssueTypeState(),
                        issue.getCreatedAt(),
                        issue.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(issues, issueResources, null);

    }

    @Override
    public Response findAllIssuesByState(String issueState, Pageable pageable, String authorization) {

        Set<Long> accountRdbmsIds = new HashSet<>();
        List<IssueResource> issueResources = new ArrayList<>();

        Page<Issue> issues = issueRepository.findAllByState(issueState, pageable);
        issues.forEach(issue -> accountRdbmsIds.add(issue.getIssueReporterAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        issues.forEach(issue -> accountResources.forEach(accountResource -> {
            if (issue.getIssueReporterAccountRdbmsId().equals(accountResource.getAccountId())) {
                issueResources.add(new IssueResource(
                        issue.getIssueRdbmsId(),
                        issue.getIssueType(),
                        accountResource,
                        issue.getIssueMessage(),
                        issue.getIssueTypeState(),
                        issue.getCreatedAt(),
                        issue.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(issues, issueResources, null);

    }

    @Override
    public Response findAllIssuesByKeyword(String keyword, Pageable pageable, String authorization) {

        Set<Long> accountRdbmsIds = new HashSet<>();
        List<IssueResource> issueResources = new ArrayList<>();

        Page<Issue> issues = issueRepository.findAllByState(keyword, pageable);
        issues.forEach(issue -> accountRdbmsIds.add(issue.getIssueReporterAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        issues.forEach(issue -> accountResources.forEach(accountResource -> {
            if (issue.getIssueReporterAccountRdbmsId().equals(accountResource.getAccountId())) {
                issueResources.add(new IssueResource(
                        issue.getIssueRdbmsId(),
                        issue.getIssueType(),
                        accountResource,
                        issue.getIssueMessage(),
                        issue.getIssueTypeState(),
                        issue.getCreatedAt(),
                        issue.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(issues, issueResources, null);

    }

    @Override
    public Response findAllIssuesByMultipleRdbmsIds(Set<Short> multipleRdbmsIds, Pageable pageable, String authorization) {

        Set<Long> accountRdbmsIds = new HashSet<>();
        List<IssueResource> issueResources = new ArrayList<>();

        Page<Issue> issues = issueRepository.findAllByMultipleIds(multipleRdbmsIds, pageable);
        issues.forEach(issue -> accountRdbmsIds.add(issue.getIssueReporterAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        issues.forEach(issue -> accountResources.forEach(accountResource -> {
            if (issue.getIssueReporterAccountRdbmsId().equals(accountResource.getAccountId())) {
                issueResources.add(new IssueResource(
                        issue.getIssueRdbmsId(),
                        issue.getIssueType(),
                        accountResource,
                        issue.getIssueMessage(),
                        issue.getIssueTypeState(),
                        issue.getCreatedAt(),
                        issue.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(issues, issueResources, null);

    }

    @Override
    public Boolean exists(Long issueRdbmsId) {
        return issueRepository.findById(issueRdbmsId).isPresent();
    }

    @Override
    public Boolean exists(Long accountRdbmsId, Short issueTypeRdbmsId, String issueMessage) {

        return issueRepository.findByIssueReporterAccountIdAndIssueTypeId(accountRdbmsId, issueTypeRdbmsId)
                .map(issue -> issue.getIssueMessage().equals(issueMessage)).orElse(false);

    }

    @Override
    public Boolean issueTypeExists(Short issueTypeRdbmsId) {
        return issueTypeService.exists(issueTypeRdbmsId);
    }

    @Override
    public Boolean accountExists(Long accountRdbmsId, String authorization) {
        return mySQLApiService.doesAccountExist(accountRdbmsId, authorization);
    }

    @Override
    public Long countTotalIssues() {
        return issueRepository.count();
    }

    @Override
    public Long countTotalIssuesByIssueTypeRdbmsId(Short issueTypeRdbmsId) {
        return issueRepository.countTotalByIssueTypeId(issueTypeRdbmsId);
    }

    @Override
    public Long countTotalIssuesByIssueTypeRdbmsIdAndState(Short issueTypeRdbmsId, String issueState) {
        return issueRepository.countTotalByIssueTypeIdAndState(issueTypeRdbmsId, issueState);
    }

    @Override
    public Long countTotalIssuesByIssueReporterAccountRdbmsId(Long issueReporterAccountRdbmsId) {
        return issueRepository.countTotalByIssueReporterAccountId(issueReporterAccountRdbmsId);
    }

    @Override
    public Long countTotalIssuesByIssueReporterAccountRdbmsIdAndState(Long issueReporterAccountRdbmsId, String issueState) {
        return issueRepository.countTotalByIssueReporterAccountIdAndState(issueReporterAccountRdbmsId, issueState);
    }

    @Override
    public Long countTotalIssuesByState(String issueState) {
        return issueRepository.countTotalByState(issueState);
    }

}