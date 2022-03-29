package com.simplenazmul.com.feedbackapi.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simplenazmul.com.feedbackapi.entity.Feedback;
import com.simplenazmul.com.feedbackapi.exception.InternalServerErrorException;
import com.simplenazmul.com.feedbackapi.exception.NotFoundException;
import com.simplenazmul.com.feedbackapi.helper.Helper;
import com.simplenazmul.com.feedbackapi.helper.Response;
import com.simplenazmul.com.feedbackapi.helper.State;
import com.simplenazmul.com.feedbackapi.repository.FeedbackRepository;
import com.simplenazmul.com.feedbackapi.resource.AccountResource;
import com.simplenazmul.com.feedbackapi.resource.FeedbackResource;

@Service
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

    Logger logger = LoggerFactory.getLogger(FeedbackServiceImpl.class);

    @Autowired
    private HelperService helperService;

    @Autowired
    private MySQLApiService mySQLApiService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public Feedback saveFeedback(Long feedbackGiverAccountRdbmsId, String feedback) {

        Timestamp timestamp = Helper.getCurrentTimestamp();

        Feedback theFeedback = new Feedback();
        theFeedback.setFeedbackGiverAccountRdbmsId(feedbackGiverAccountRdbmsId);
        theFeedback.setFeedback(feedback);
        theFeedback.setFeedbackState(State.Active.toString());
        theFeedback.setCreatedAt(timestamp);
        theFeedback.setUpdatedAt(timestamp);

        try {
            return feedbackRepository.save(theFeedback);
        } catch (Exception e) {
            logger.error("Error in saving feedback. ERROR: " + e.getMessage());
            throw new InternalServerErrorException("Something went wrong on the server!");
        }

    }

    @Override
    public void updateFeedbackByRdbmsId(String feedback, Long feedbackRdbmsId) {

        try {
            feedbackRepository.updateFeedbackById(feedback, feedbackRdbmsId);
        } catch (Exception e) {
            logger.info("Error in updating feedback. ERROR: " + e.getMessage());
            throw new InternalServerErrorException("Something went wrong on the server!");
        }

    }

    @Override
    public void updateFeedbackStateByRdbmsId(String feedbackState, Long feedbackRdbmsId) {

        try {
            feedbackRepository.updateStateById(feedbackState, feedbackRdbmsId);
        } catch (Exception e) {
            logger.info("Error in updating feedback state. ERROR: " + e.getMessage());
            throw new InternalServerErrorException("Something went wrong on the server!");
        }

    }

    @Override
    public void deleteFeedbackByRdbmsId(Long feedbackRdbmsId) {
        feedbackRepository.deleteById(feedbackRdbmsId);
    }

    @Override
    public FeedbackResource findFeedbackByRdbmsId(Long feedbackRdbmsId, String authorization) {

        Set<Long> accountRdbmsIds = new HashSet<>();
        Feedback feedback = feedbackRepository.findById(feedbackRdbmsId)
                .orElseThrow(() -> new NotFoundException("No feedback with this feedbackRdbmsId is found!"));
        accountRdbmsIds.add(feedback.getFeedbackGiverAccountRdbmsId());

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        return new FeedbackResource(
                feedback.getFeedbackRdbmsId(),
                accountResources.get(0),
                feedback.getFeedback(),
                feedback.getFeedbackState(),
                feedback.getCreatedAt(),
                feedback.getUpdatedAt()
        );

    }

    @Override
    public String findFeedbackStateByRdbmsId(Long feedbackRdbmsId) {
        return feedbackRepository.findStateById(feedbackRdbmsId);
    }

    @Override
    public Response findAllFeedbacks(Pageable pageable, String authorization) {

        Set<Long> accountRdbmsIds = new HashSet<>();
        List<FeedbackResource> feedbackResources = new ArrayList<>();

        Page<Feedback> feedbacks = feedbackRepository.findAllFeedbacks(pageable);
        feedbacks.forEach(feedback -> accountRdbmsIds.add(feedback.getFeedbackGiverAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        feedbacks.forEach(feedback -> accountResources.forEach(accountResource -> {
            if (feedback.getFeedbackGiverAccountRdbmsId().equals(accountResource.getAccountId())) {
                feedbackResources.add(new FeedbackResource(
                        feedback.getFeedbackRdbmsId(),
                        accountResource,
                        feedback.getFeedback(),
                        feedback.getFeedbackState(),
                        feedback.getCreatedAt(),
                        feedback.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(feedbacks, feedbackResources, null);

    }

    @Override
    public Response findAllFeedbacksByFeedbackGiverAccountRdbmsId(Long feedbackGiverAccountRdbmsId, Pageable pageable, String authorization) {

        Set<Long> accountRdbmsIds = new HashSet<>();
        List<FeedbackResource> feedbackResources = new ArrayList<>();

        Page<Feedback> feedbacks = feedbackRepository.findAllByFeedbackGiverAccountId(feedbackGiverAccountRdbmsId, pageable);
        feedbacks.forEach(feedback -> accountRdbmsIds.add(feedback.getFeedbackGiverAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        feedbacks.forEach(feedback -> accountResources.forEach(accountResource -> {
            if (feedback.getFeedbackGiverAccountRdbmsId().equals(accountResource.getAccountId())) {
                feedbackResources.add(new FeedbackResource(
                        feedback.getFeedbackRdbmsId(),
                        accountResource,
                        feedback.getFeedback(),
                        feedback.getFeedbackState(),
                        feedback.getCreatedAt(),
                        feedback.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(feedbacks, feedbackResources, null);

    }

    @Override
    public Response findAllFeedbacksByFeedbackGiverAccountRdbmsIdAndState(Long feedbackGiverAccountRdbmsId, String feedbackState, Pageable pageable, String authorization) {

        Set<Long> accountRdbmsIds = new HashSet<>();
        List<FeedbackResource> feedbackResources = new ArrayList<>();

        Page<Feedback> feedbacks = feedbackRepository.findAllByFeedbackGiverAccountIdAndState(feedbackGiverAccountRdbmsId, feedbackState, pageable);
        feedbacks.forEach(feedback -> accountRdbmsIds.add(feedback.getFeedbackGiverAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        feedbacks.forEach(feedback -> accountResources.forEach(accountResource -> {
            if (feedback.getFeedbackGiverAccountRdbmsId().equals(accountResource.getAccountId())) {
                feedbackResources.add(new FeedbackResource(
                        feedback.getFeedbackRdbmsId(),
                        accountResource,
                        feedback.getFeedback(),
                        feedback.getFeedbackState(),
                        feedback.getCreatedAt(),
                        feedback.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(feedbacks, feedbackResources, null);

    }

    @Override
    public Response findAllFeedbacksByState(String feedbackState, Pageable pageable, String authorization) {

        Set<Long> accountRdbmsIds = new HashSet<>();
        List<FeedbackResource> feedbackResources = new ArrayList<>();

        Page<Feedback> feedbacks = feedbackRepository.findAllByState(feedbackState, pageable);
        feedbacks.forEach(feedback -> accountRdbmsIds.add(feedback.getFeedbackGiverAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        feedbacks.forEach(feedback -> accountResources.forEach(accountResource -> {
            if (feedback.getFeedbackGiverAccountRdbmsId().equals(accountResource.getAccountId())) {
                feedbackResources.add(new FeedbackResource(
                        feedback.getFeedbackRdbmsId(),
                        accountResource,
                        feedback.getFeedback(),
                        feedback.getFeedbackState(),
                        feedback.getCreatedAt(),
                        feedback.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(feedbacks, feedbackResources, null);

    }

    @Override
    public Response findAllFeedbacksByKeyword(String keyword, Pageable pageable, String authorization) {

        Set<Long> accountRdbmsIds = new HashSet<>();
        List<FeedbackResource> feedbackResources = new ArrayList<>();

        Page<Feedback> feedbacks = feedbackRepository.findAllByKeyword(keyword, pageable);
        feedbacks.forEach(feedback -> accountRdbmsIds.add(feedback.getFeedbackGiverAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        feedbacks.forEach(feedback -> accountResources.forEach(accountResource -> {
            if (feedback.getFeedbackGiverAccountRdbmsId().equals(accountResource.getAccountId())) {
                feedbackResources.add(new FeedbackResource(
                        feedback.getFeedbackRdbmsId(),
                        accountResource,
                        feedback.getFeedback(),
                        feedback.getFeedbackState(),
                        feedback.getCreatedAt(),
                        feedback.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(feedbacks, feedbackResources, null);

    }

    @Override
    public Response findAllFeedbacksByMultipleRdbmsIds(Set<Short> multipleRdbmsIds, Pageable pageable, String authorization) {

        Set<Long> accountRdbmsIds = new HashSet<>();
        List<FeedbackResource> feedbackResources = new ArrayList<>();

        Page<Feedback> feedbacks = feedbackRepository.findAllByMultipleIds(multipleRdbmsIds, pageable);
        feedbacks.forEach(feedback -> accountRdbmsIds.add(feedback.getFeedbackGiverAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        feedbacks.forEach(feedback -> accountResources.forEach(accountResource -> {
            if (feedback.getFeedbackGiverAccountRdbmsId().equals(accountResource.getAccountId())) {
                feedbackResources.add(new FeedbackResource(
                        feedback.getFeedbackRdbmsId(),
                        accountResource,
                        feedback.getFeedback(),
                        feedback.getFeedbackState(),
                        feedback.getCreatedAt(),
                        feedback.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(feedbacks, feedbackResources, null);

    }

    @Override
    public Response findAllFeedbacksOfTheDay(String dateString, Pageable pageable, String authorization) {
        Set<Long> accountRdbmsIds = new HashSet<>();
        List<FeedbackResource> feedbackResources = new ArrayList<>();

        Page<Feedback> feedbacks = feedbackRepository.findAllOfTheDay(dateString, pageable);
        feedbacks.forEach(feedback -> accountRdbmsIds.add(feedback.getFeedbackGiverAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        feedbacks.forEach(feedback -> accountResources.forEach(accountResource -> {
            if (feedback.getFeedbackGiverAccountRdbmsId().equals(accountResource.getAccountId())) {
                feedbackResources.add(new FeedbackResource(
                        feedback.getFeedbackRdbmsId(),
                        accountResource,
                        feedback.getFeedback(),
                        feedback.getFeedbackState(),
                        feedback.getCreatedAt(),
                        feedback.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(feedbacks, feedbackResources, null);
    }

    @Override
    public Response findAllFeedbacksOfTheWeek(String dateString, Pageable pageable, String authorization) {
        Set<Long> accountRdbmsIds = new HashSet<>();
        List<FeedbackResource> feedbackResources = new ArrayList<>();

        Page<Feedback> feedbacks = feedbackRepository.findAllOfTheWeek(dateString, pageable);
        feedbacks.forEach(feedback -> accountRdbmsIds.add(feedback.getFeedbackGiverAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        feedbacks.forEach(feedback -> accountResources.forEach(accountResource -> {
            if (feedback.getFeedbackGiverAccountRdbmsId().equals(accountResource.getAccountId())) {
                feedbackResources.add(new FeedbackResource(
                        feedback.getFeedbackRdbmsId(),
                        accountResource,
                        feedback.getFeedback(),
                        feedback.getFeedbackState(),
                        feedback.getCreatedAt(),
                        feedback.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(feedbacks, feedbackResources, null);
    }

    @Override
    public Response findAllFeedbacksOfTheMonth(String dateString, Pageable pageable, String authorization) {
        Set<Long> accountRdbmsIds = new HashSet<>();
        List<FeedbackResource> feedbackResources = new ArrayList<>();

        Page<Feedback> feedbacks = feedbackRepository.findAllOfTheMonth(dateString, pageable);
        feedbacks.forEach(feedback -> accountRdbmsIds.add(feedback.getFeedbackGiverAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        feedbacks.forEach(feedback -> accountResources.forEach(accountResource -> {
            if (feedback.getFeedbackGiverAccountRdbmsId().equals(accountResource.getAccountId())) {
                feedbackResources.add(new FeedbackResource(
                        feedback.getFeedbackRdbmsId(),
                        accountResource,
                        feedback.getFeedback(),
                        feedback.getFeedbackState(),
                        feedback.getCreatedAt(),
                        feedback.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(feedbacks, feedbackResources, null);
    }

    @Override
    public Response findAllFeedbacksOfTheYear(String dateString, Pageable pageable, String authorization) {
        Set<Long> accountRdbmsIds = new HashSet<>();
        List<FeedbackResource> feedbackResources = new ArrayList<>();

        Page<Feedback> feedbacks = feedbackRepository.findAllOfTheYear(dateString, pageable);
        feedbacks.forEach(feedback -> accountRdbmsIds.add(feedback.getFeedbackGiverAccountRdbmsId()));

        ParameterizedTypeReference<List<AccountResource>> typeReference = new ParameterizedTypeReference<>() {};
        List<AccountResource> accountResources = mySQLApiService.findAccountInformationByMultipleRdbmsIds(accountRdbmsIds, typeReference, authorization);

        feedbacks.forEach(feedback -> accountResources.forEach(accountResource -> {
            if (feedback.getFeedbackGiverAccountRdbmsId().equals(accountResource.getAccountId())) {
                feedbackResources.add(new FeedbackResource(
                        feedback.getFeedbackRdbmsId(),
                        accountResource,
                        feedback.getFeedback(),
                        feedback.getFeedbackState(),
                        feedback.getCreatedAt(),
                        feedback.getUpdatedAt()
                ));
            }
        }));

        return helperService.getResponse(feedbacks, feedbackResources, null);
    }

    @Override
    public Boolean exists(Long feedbackRdbmsId) {

        return feedbackRepository.findById(feedbackRdbmsId).isPresent();

    }

    @Override
    public Boolean exists(Long accountRdbmsId, String feedback) {

        return feedbackRepository.findByFeedbackGiverAccountId(accountRdbmsId).orElseThrow().getFeedback().equals(feedback);

    }

    @Override
    public Boolean accountExists(Long accountRdbmsId, String authorization) {

        return mySQLApiService.doesAccountExist(accountRdbmsId, authorization);

    }

    @Override
    public Long countTotalFeedbacks() {

        return feedbackRepository.count();

    }

    @Override
    public Long countTotalFeedbacksOfTheDay(String dateString) {
        return feedbackRepository.countTotalOfTheDay(dateString);
    }

    @Override
    public Long countTotalFeedbacksOfTheWeek(String dateString) {
        return feedbackRepository.countTotalOfTheWeek(dateString);
    }

    @Override
    public Long countTotalFeedbacksOfTheMonth(String dateString) {
        return feedbackRepository.countTotalOfTheMonth(dateString);
    }

    @Override
    public Long countTotalFeedbacksOfTheYear(String dateString) {
        return feedbackRepository.countTotalOfTheYear(dateString);
    }

    @Override
    public Long countTotalFeedbacksByFeedbackGiverAccountRdbmsId(Long feedbackGiverAccountRdbmsId) {

        return feedbackRepository.countTotalByFeedbackGiverAccountId(feedbackGiverAccountRdbmsId);

    }

    @Override
    public Long countTotalFeedbacksByFeedbackGiverAccountRdbmsIdAndState(Long feedbackGiverAccountRdbmsId, String feedbackState) {

        return feedbackRepository.countTotalByFeedbackGiverAccountIdAndState(feedbackGiverAccountRdbmsId, feedbackState);

    }

    @Override
    public Long countTotalFeedbacksByState(String feedbackState) {

        return feedbackRepository.countTotalByState(feedbackState);

    }

}