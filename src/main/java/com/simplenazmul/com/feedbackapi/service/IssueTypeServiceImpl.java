package com.simplenazmul.com.feedbackapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simplenazmul.com.feedbackapi.entity.IssueType;
import com.simplenazmul.com.feedbackapi.exception.InternalServerErrorException;
import com.simplenazmul.com.feedbackapi.exception.NotFoundException;
import com.simplenazmul.com.feedbackapi.helper.Helper;
import com.simplenazmul.com.feedbackapi.helper.Response;
import com.simplenazmul.com.feedbackapi.helper.State;
import com.simplenazmul.com.feedbackapi.repository.IssueTypeRepository;

import java.sql.Timestamp;
import java.util.Set;

@Service
@Transactional
public class IssueTypeServiceImpl implements IssueTypeService {

    Logger logger = LoggerFactory.getLogger(IssueTypeServiceImpl.class);

    @Autowired
    private HelperService helperService;

    @Autowired
    private IssueTypeRepository issueTypeRepository;

    @Override
    public IssueType saveIssueType(String issueTypeName, String issueTypeUrlName) {

        Timestamp timestamp = Helper.getCurrentTimestamp();

        IssueType issueType = new IssueType();
        issueType.setIssueTypeName(issueTypeName);
        issueType.setIssueTypeUrlName(issueTypeUrlName);
        issueType.setIssueTypeState(State.Inactive.name());
        issueType.setCreatedAt(timestamp);
        issueType.setUpdatedAt(timestamp);

        try {
            return issueTypeRepository.save(issueType);
        } catch (Exception e) {
            logger.error("Error in saving issue type. ERROR: " + e.getMessage());
            throw new InternalServerErrorException("Something went wrong on the server!");
        }

    }

    @Override
    public void updateIssueTypeNameByRdbmsId(String issueTypeName, Short issueTypeRdbmsId) {

        try {
            issueTypeRepository.updateNameById(issueTypeName, issueTypeRdbmsId);
        } catch (Exception e) {
            logger.info("Error in updating issue type name. ERROR: " + e.getMessage());
            throw new InternalServerErrorException("Something went wrong on the server!");
        }

    }

    @Override
    public void updateIssueTypeUrlNameByRdbmsId(String issueTypeUrlName, Short issueTypeRdbmsId) {

        try {
            issueTypeRepository.updateUrlNameById(Helper.toSlugCase(issueTypeUrlName), issueTypeRdbmsId);
        } catch (Exception e) {
            logger.info("Error in updating issue type url name. ERROR: " + e.getMessage());
            throw new InternalServerErrorException("Something went wrong on the server!");
        }

    }

    @Override
    public void updateIssueTypeStateByRdbmsId(String issueTypeState, Short issueTypeRdbmsId) {

        try {
            issueTypeRepository.updateStateById(issueTypeState, issueTypeRdbmsId);
        } catch (Exception e) {
            logger.info("Error in updating issue type state. ERROR: " + e.getMessage());
            throw new InternalServerErrorException("Something went wrong on the server!");
        }

    }

    @Override
    public void deleteIssueTypeByRdbmsId(Short issueTypeRdbmsId) {
        issueTypeRepository.deleteById(issueTypeRdbmsId);
    }

    @Override
    public IssueType findIssueTypeByRdbmsId(Short issueTypeRdbmsId) {

        return issueTypeRepository.findById(issueTypeRdbmsId)
                .orElseThrow(() -> new NotFoundException("No issue type with this issueTypeRdbmsId is found!"));

    }

    @Override
    public IssueType findIssueTypeByName(String issueTypeName) {

        return issueTypeRepository.findByName(issueTypeName)
                .orElseThrow(() -> new NotFoundException("No issue type with this issueTypeName is found!"));

    }

    @Override
    public IssueType findIssueTypeByUrlName(String issueTypeUrlName) {

        return issueTypeRepository.findByUrlName(Helper.toSlugCase(issueTypeUrlName))
                .orElseThrow(() -> new NotFoundException("No issue type with this issueTypeUrlName is found!"));

    }

    @Override
    public String findIssueTypeStateByRdbmsId(Short issueTypeRdbmsId) {
        return issueTypeRepository.findStateById(issueTypeRdbmsId);
    }

    @Override
    public Response findAllIssueTypes(Pageable pageable) {
        Page<IssueType> issueTypes = issueTypeRepository.findAll(pageable);
        return helperService.getResponse(issueTypes, null, null);
    }

    @Override
    public Response findAllIssueTypesByState(String issueTypeState, Pageable pageable) {
        Page<IssueType> issueTypes = issueTypeRepository.findAllByState(issueTypeState, pageable);
        return helperService.getResponse(issueTypes, null, null);
    }

    @Override
    public Response findAllIssueTypesByKeyword(String keyword, Pageable pageable) {
        Page<IssueType> issueTypes = issueTypeRepository.findAllByKeyword(keyword, pageable);
        return helperService.getResponse(issueTypes, null, null);
    }

    @Override
    public Response findAllIssueTypesByMultipleRdbmsIds(Set<Short> multipleRdbmsIds, Pageable pageable) {
        Page<IssueType> issueTypes = issueTypeRepository.findAllByMultipleIds(multipleRdbmsIds, pageable);
        return helperService.getResponse(issueTypes, null, null);
    }

    @Override
    public Boolean exists(Short issueTypeRdbmsId) {
        return issueTypeRepository.findById(issueTypeRdbmsId).isPresent();
    }

    @Override
    public Boolean nameExists(String issueTypeName) {
        return issueTypeRepository.findByName(issueTypeName).isPresent();
    }

    @Override
    public Boolean urlNameExists(String issueTypeUrlName) {
        return issueTypeRepository.findByUrlName(Helper.toSlugCase(issueTypeUrlName)).isPresent();
    }

    @Override
    public Long countTotalIssueTypes() {
        return issueTypeRepository.count();
    }

    @Override
    public Short countTotalIssueTypesByState(String issueTypeState) {
        return issueTypeRepository.countTotalByState(issueTypeState);
    }

}