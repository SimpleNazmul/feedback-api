package com.simplenazmul.com.feedbackapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.simplenazmul.com.feedbackapi.entity.Feedback;
import com.simplenazmul.com.feedbackapi.exception.BadRequestException;
import com.simplenazmul.com.feedbackapi.exception.NotFoundException;
import com.simplenazmul.com.feedbackapi.exception.UnprocessableEntityException;
import com.simplenazmul.com.feedbackapi.helper.Helper;
import com.simplenazmul.com.feedbackapi.helper.Response;
import com.simplenazmul.com.feedbackapi.helper.State;
import com.simplenazmul.com.feedbackapi.resource.FeedbackResource;
import com.simplenazmul.com.feedbackapi.service.FeedbackService;
import com.simplenazmul.com.feedbackapi.service.KeycloakApiService;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/feedback-api/v1/feedbacks")
@CrossOrigin(origins = {"https://localhost:2000"},
        allowCredentials = "true", maxAge = 3600, allowedHeaders = "*", exposedHeaders = {"X-Total-Count", "first", "last", "next", "prev"},
        methods = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.GET, RequestMethod.OPTIONS })
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private KeycloakApiService keycloakApiService;

    private static final int MAX_PAGE_SIZE = 20;

    @RolesAllowed("all_user")
    @PostMapping("/save/")
    public ResponseEntity<Feedback> saveFeedback(@RequestHeader("Authorization") String authorization, @RequestParam("feedback") String feedbackString) {

        if (feedbackString == null || feedbackString.isBlank()) {
            throw new UnprocessableEntityException("feedback field is required!");
        }

        String feedback = feedbackString.trim();

        if (feedback.length() > 500) {
            throw new UnprocessableEntityException("feedback must be less than or equal to 500 characters!");
        }

        Long accountRdbmsId = keycloakApiService.findAuthenticatedUser(authorization).getAccountId();

        if (feedbackService.exists(accountRdbmsId, feedback)) {
            throw new BadRequestException("You already submitted this feedback. Thank you!");
        }

        return new ResponseEntity<>(feedbackService.saveFeedback(accountRdbmsId, feedback), HttpStatus.CREATED);

    }

    @RolesAllowed("super_admin")
    @PutMapping("/update/feedback-state/by/feedback-id/{feedbackRdbmsId}")
    public ResponseEntity<Void> updateFeedbackStateByRdbmsId(@RequestHeader("Authorization") String authorization, @PathVariable("feedbackRdbmsId") Long feedbackRdbmsId, @RequestParam("feedbackState") String feedbackState) {

        if (feedbackState == null || (!feedbackState.equals(State.Active.toString()) && !feedbackState.equals(State.Disabled.toString()))) {
            throw new UnprocessableEntityException("Only 'Active', 'Inactive', 'Disabled' values are allowed as feedbackState!");
        }

        if (!feedbackService.exists(feedbackRdbmsId)) {
            throw new NotFoundException("No feedback with this feedbackRdbmsId is found!");
        }

        feedbackService.updateFeedbackStateByRdbmsId(feedbackState, feedbackRdbmsId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);

    }

    @RolesAllowed("super_admin")
    @DeleteMapping("/delete/temporary/by/feedback-id/{feedbackRdbmsId}")
    public ResponseEntity<Void> deleteFeedbackTemporarilyByRdbmsId(@RequestHeader("Authorization") String authorization, @PathVariable("feedbackRdbmsId") Long feedbackRdbmsId) {

        if (!feedbackService.exists(feedbackRdbmsId)) {
            throw new NotFoundException("No feedback with this feedbackRdbmsId is found!");
        }

        feedbackService.updateFeedbackStateByRdbmsId(State.Deleted.toString(), feedbackRdbmsId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);

    }

    @RolesAllowed("super_admin")
    @DeleteMapping("/delete/permanent/by/feedback-id/{feedbackRdbmsId}")
    public ResponseEntity<Void> deleteFeedbackPermanentlyByRdbmsId(@RequestHeader("Authorization") String authorization, @PathVariable("feedbackRdbmsId") Long feedbackRdbmsId) {

        if (!feedbackService.exists(feedbackRdbmsId)) {
            throw new NotFoundException("No feedback with this feedbackRdbmsId is found!");
        }

        if (!feedbackService.findFeedbackStateByRdbmsId(feedbackRdbmsId).equals(State.Deleted.toString())) {
            throw new BadRequestException("This feedback can not be deleted permanently! Can be deleted temporarily.");
        }

        feedbackService.deleteFeedbackByRdbmsId(feedbackRdbmsId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @RolesAllowed("super_admin")
    @GetMapping("/find/by/feedback-id/{feedbackRdbmsId}")
    public ResponseEntity<FeedbackResource> findFeedbackByRdbmsId(@RequestHeader("Authorization") String authorization, @PathVariable("feedbackRdbmsId") Long feedbackRdbmsId) {

        return new ResponseEntity<>(feedbackService.findFeedbackByRdbmsId(feedbackRdbmsId, authorization), HttpStatus.OK);

    }

    @RolesAllowed("super_admin")
    @GetMapping("/find/all/")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<FeedbackResource>> findAllFeedbacks(@RequestHeader("Authorization") String authorization, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        Response response = feedbackService.findAllFeedbacks(pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<FeedbackResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("all_user")
    @GetMapping("/find/all/by/feedback-reporter-id/")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<FeedbackResource>> findAllFeedbacksByFeedbackGiverAccountRdbmsId(@RequestHeader("Authorization") String authorization, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        Response response = feedbackService.findAllFeedbacksByFeedbackGiverAccountRdbmsId(keycloakApiService.findAuthenticatedUser(authorization).getAccountId(), pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<FeedbackResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @GetMapping("/find/all/by/feedback-reporter-id/{feedbackGiverAccountRdbmsId}/and/feedback-state/{feedbackState}")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<FeedbackResource>> findAllFeedbacksByFeedbackGiverAccountRdbmsIdAndState(@RequestHeader("Authorization") String authorization, @PathVariable("feedbackGiverAccountRdbmsId") Long feedbackGiverAccountRdbmsId, @PathVariable("feedbackState") String feedbackState, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        if (feedbackState == null || (!feedbackState.equals(State.Active.toString()) && !feedbackState.equals(State.Inactive.toString()) &&
                !feedbackState.equals(State.Disabled.toString()) && !feedbackState.equals(State.Deleted.toString()))) {
            throw new BadRequestException("Invalid feedbackState value! Only Active, Inactive, Disabled Or Deleted value is allowed.");
        }

        if (!feedbackService.accountExists(feedbackGiverAccountRdbmsId, authorization)) {
            throw new NotFoundException("No user account with this feedbackGiverAccountRdbmsId is found!");
        }

        Response response = feedbackService.findAllFeedbacksByFeedbackGiverAccountRdbmsIdAndState(feedbackGiverAccountRdbmsId, feedbackState, pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<FeedbackResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @GetMapping("/find/all/by/feedback-state/{feedbackState}")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<FeedbackResource>> findAllFeedbacksByState(@RequestHeader("Authorization") String authorization, @PathVariable("feedbackState") String feedbackState, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        if (feedbackState == null || (!feedbackState.equals(State.Active.toString()) && !feedbackState.equals(State.Inactive.toString()) &&
                !feedbackState.equals(State.Disabled.toString()) && !feedbackState.equals(State.Deleted.toString()))) {
            throw new BadRequestException("Invalid feedbackState value! Only Active, Inactive, Disabled Or Deleted value is allowed.");
        }

        Response response = feedbackService.findAllFeedbacksByState(feedbackState, pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<FeedbackResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @PostMapping("/find/all/by/keyword/")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<FeedbackResource>> findAllFeedbacksByKeyword(@RequestHeader("Authorization") String authorization, @RequestParam("keyword") String keyword, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        if (!Helper.isValidKeyword(keyword)) {
            throw new BadRequestException("Invalid keyword!");
        }

        Response response = feedbackService.findAllFeedbacksByKeyword(keyword, pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<FeedbackResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @PostMapping("/find/all/by/multiple-ids/")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<FeedbackResource>> findAllFeedbacksByMultipleRdbmsIds(@RequestHeader("Authorization") String authorization, @RequestParam("multipleRdbmsIds") Set<Short> multipleRdbmsIds, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        Response response = feedbackService.findAllFeedbacksByMultipleRdbmsIds(multipleRdbmsIds, pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<FeedbackResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @PostMapping("/find/all/of/the/day/")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<FeedbackResource>> findAllFeedbacksOfTheDay(@RequestHeader("Authorization") String authorization,
                                                                           @RequestParam("dateString") String dateString,
                                                                           @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        if (!Helper.isCorrectDateFormat(dateString)) {
            throw new BadRequestException("Invalid dateString! Valid format is 'yyyy-MM-dd'.");
        }

        Response response = feedbackService.findAllFeedbacksOfTheDay(dateString, pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<FeedbackResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @PostMapping("/find/all/of/the/week/")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<FeedbackResource>> findAllFeedbacksOfTheWeek(@RequestHeader("Authorization") String authorization,
                                                                            @RequestParam("dateString") String dateString,
                                                                            @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        if (!Helper.isCorrectDateFormat(dateString)) {
            throw new BadRequestException("Invalid dateString! Valid format is 'yyyy-MM-dd'.");
        }

        Response response = feedbackService.findAllFeedbacksOfTheWeek(dateString, pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<FeedbackResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @PostMapping("/find/all/of/the/month/")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<FeedbackResource>> findAllFeedbacksOfTheMonth(@RequestHeader("Authorization") String authorization,
                                                                             @RequestParam("dateString") String dateString,
                                                                             @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        if (!Helper.isCorrectDateFormat(dateString)) {
            throw new BadRequestException("Invalid dateString! Valid format is 'yyyy-MM-dd'.");
        }

        Response response = feedbackService.findAllFeedbacksOfTheMonth(dateString, pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<FeedbackResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @PostMapping("/find/all/of/the/year/")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<FeedbackResource>> findAllFeedbacksOfTheYear(@RequestHeader("Authorization") String authorization,
                                                                            @RequestParam("dateString") String dateString,
                                                                            @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        if (!Helper.isCorrectDateFormat(dateString)) {
            throw new BadRequestException("Invalid dateString! Valid format is 'yyyy-MM-dd'.");
        }

        Response response = feedbackService.findAllFeedbacksOfTheYear(dateString, pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<FeedbackResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @GetMapping("/count/all/")
    public ResponseEntity<Long> countAllFeedbacks(@RequestHeader("Authorization") String authorization) {

        return new ResponseEntity<>(feedbackService.countTotalFeedbacks(), HttpStatus.OK);

    }

    @RolesAllowed("super_admin")
    @PostMapping("/count/all/of/the/day/")
    public ResponseEntity<Long> countAllFeedbacksOfTheDay(@RequestHeader("Authorization") String authorization,
                                                          @RequestParam("dateString") String dateString) {

        if (!Helper.isCorrectDateFormat(dateString)) {
            throw new BadRequestException("Invalid dateString! Valid format is 'yyyy-MM-dd'.");
        }

        return new ResponseEntity<>(feedbackService.countTotalFeedbacksOfTheDay(dateString), HttpStatus.OK);

    }

    @RolesAllowed("super_admin")
    @PostMapping("/count/all/of/the/week/")
    public ResponseEntity<Long> countAllFeedbacksOfTheWeek(@RequestHeader("Authorization") String authorization,
                                                           @RequestParam("dateString") String dateString) {

        if (!Helper.isCorrectDateFormat(dateString)) {
            throw new BadRequestException("Invalid dateString! Valid format is 'yyyy-MM-dd'.");
        }

        return new ResponseEntity<>(feedbackService.countTotalFeedbacksOfTheWeek(dateString), HttpStatus.OK);

    }

    @RolesAllowed("super_admin")
    @PostMapping("/count/all/of/the/month/")
    public ResponseEntity<Long> countAllFeedbacksOfTheMonth(@RequestHeader("Authorization") String authorization,
                                                            @RequestParam("dateString") String dateString) {

        if (!Helper.isCorrectDateFormat(dateString)) {
            throw new BadRequestException("Invalid dateString! Valid format is 'yyyy-MM-dd'.");
        }

        return new ResponseEntity<>(feedbackService.countTotalFeedbacksOfTheMonth(dateString), HttpStatus.OK);

    }

    @RolesAllowed("super_admin")
    @PostMapping("/count/all/of/the/year/")
    public ResponseEntity<Long> countAllFeedbacksOfTheYear(@RequestHeader("Authorization") String authorization,
                                                           @RequestParam("dateString") String dateString) {

        if (!Helper.isCorrectDateFormat(dateString)) {
            throw new BadRequestException("Invalid dateString! Valid format is 'yyyy-MM-dd'.");
        }

        return new ResponseEntity<>(feedbackService.countTotalFeedbacksOfTheYear(dateString), HttpStatus.OK);

    }

    @RolesAllowed("all_user")
    @GetMapping("/count/all/by/feedback-reporter-id/")
    public ResponseEntity<Long> countAllSchoolsByFeedbackGiverAccountRdbmsId(@RequestHeader("Authorization") String authorization) {

        return new ResponseEntity<>(feedbackService.countTotalFeedbacksByFeedbackGiverAccountRdbmsId(keycloakApiService.findAuthenticatedUser(authorization).getAccountId()), HttpStatus.OK);

    }

    @RolesAllowed("super_admin")
    @GetMapping("/count/all/by/feedback-reporter-id/{feedbackGiverAccountRdbmsId}/and/feedback-state/{feedbackState}")
    public ResponseEntity<Long> countAllSchoolsByFeedbackGiverAccountRdbmsIdAndState(@RequestHeader("Authorization") String authorization, @PathVariable("feedbackTypeRdbmsId") Long feedbackGiverAccountRdbmsId, @PathVariable("feedbackState") String feedbackState) {

        if (feedbackState == null || (!feedbackState.equals(State.Active.toString()) && !feedbackState.equals(State.Inactive.toString()) &&
                !feedbackState.equals(State.Disabled.toString()) && !feedbackState.equals(State.Deleted.toString()))) {
            throw new BadRequestException("Invalid feedbackState value! Only Active, Inactive, Disabled Or Deleted value is allowed.");
        }

        if (!feedbackService.accountExists(feedbackGiverAccountRdbmsId, authorization)) {
            throw new NotFoundException("No user account with this feedbackGiverAccountRdbmsId is found!");
        }

        return new ResponseEntity<>(feedbackService.countTotalFeedbacksByFeedbackGiverAccountRdbmsIdAndState(feedbackGiverAccountRdbmsId, feedbackState), HttpStatus.OK);

    }

    @RolesAllowed("super_admin")
    @GetMapping("/count/all/by/feedback-state/{feedbackState}")
    public ResponseEntity<Long> countAllSchoolsByState(@RequestHeader("Authorization") String authorization, @PathVariable("feedbackState") String feedbackState) {

        if (feedbackState == null || (!feedbackState.equals(State.Active.toString()) && !feedbackState.equals(State.Inactive.toString()) &&
                !feedbackState.equals(State.Disabled.toString()) && !feedbackState.equals(State.Deleted.toString()))) {
            throw new BadRequestException("Invalid feedbackState value! Only Active, Inactive, Disabled Or Deleted value is allowed.");
        }

        return new ResponseEntity<>(feedbackService.countTotalFeedbacksByState(feedbackState), HttpStatus.OK);

    }

}