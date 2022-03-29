package com.simplenazmul.com.feedbackapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.simplenazmul.com.feedbackapi.entity.Issue;
import com.simplenazmul.com.feedbackapi.exception.BadRequestException;
import com.simplenazmul.com.feedbackapi.exception.NotFoundException;
import com.simplenazmul.com.feedbackapi.exception.UnprocessableEntityException;
import com.simplenazmul.com.feedbackapi.helper.Helper;
import com.simplenazmul.com.feedbackapi.helper.Response;
import com.simplenazmul.com.feedbackapi.helper.State;
import com.simplenazmul.com.feedbackapi.resource.IssueResource;
import com.simplenazmul.com.feedbackapi.service.IssueService;
import com.simplenazmul.com.feedbackapi.service.KeycloakApiService;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/feedback-api/v1/issues")
@CrossOrigin(origins = {"https://localhost:2000"}, allowCredentials = "true", maxAge = 3600, allowedHeaders = "*", exposedHeaders = {"X-Total-Count", "first", "last", "next", "prev"},
	methods = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.GET, RequestMethod.OPTIONS })
public class IssueController {

    @Autowired
    private IssueService issueService;

    @Autowired
    private KeycloakApiService keycloakApiService;

    private static final int MAX_PAGE_SIZE = 20;

    @RolesAllowed("all_user")
    @PostMapping("/save/")
    public ResponseEntity<Issue> saveIssue(@RequestHeader("Authorization") String authorization, @RequestParam("issueTypeRdbmsId") Short issueTypeRdbmsId, @RequestParam("issueMessage") String issueMessageString) {

        if (issueTypeRdbmsId == null || issueMessageString == null || issueMessageString.isBlank()) {
            throw new UnprocessableEntityException("issueTypeRdbmsId, issueMessage fields are required!");
        }

        String issueMessage = issueMessageString.trim();

        if (issueMessage.length() > 500) {
            throw new UnprocessableEntityException("issueMessage must be less than or equal to 500 characters!");
        }

        if (!issueService.issueTypeExists(issueTypeRdbmsId)) {
            throw new NotFoundException("No issue with this issueTypeRdbmsId is found!");
        }

        Long accountRdbmsId = keycloakApiService.findAuthenticatedUser(authorization).getAccountId();

        if (issueService.exists(accountRdbmsId, issueTypeRdbmsId, issueMessage)) {
            throw new BadRequestException("You already reported this issue. Thank you!");
        }

        return new ResponseEntity<>(issueService.saveIssue(issueTypeRdbmsId, accountRdbmsId, issueMessage), HttpStatus.CREATED);

    }

    @RolesAllowed("super_admin")
    @PutMapping("/update/issue-state/by/issue-id/{issueRdbmsId}")
    public ResponseEntity<Void> updateIssueStateByRdbmsId(@RequestHeader("Authorization") String authorization, @PathVariable("issueRdbmsId") Long issueRdbmsId, @RequestParam("issueState") String issueState) {

        if (issueState == null || (!issueState.equals(State.Active.toString()) && !issueState.equals(State.Disabled.toString()))) {
            throw new UnprocessableEntityException("Only 'Active', 'Inactive', 'Disabled' values are allowed as issueState!");
        }

        if (!issueService.exists(issueRdbmsId)) {
            throw new NotFoundException("No issue with this issueRdbmsId is found!");
        }

        issueService.updateIssueStateByRdbmsId(issueState, issueRdbmsId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);

    }

    @RolesAllowed("super_admin")
    @DeleteMapping("/delete/temporary/by/issue-id/{issueRdbmsId}")
    public ResponseEntity<Void> deleteIssueTemporarilyByRdbmsId(@RequestHeader("Authorization") String authorization, @PathVariable("issueRdbmsId") Long issueRdbmsId) {

        if (!issueService.exists(issueRdbmsId)) {
            throw new NotFoundException("No issue with this issueRdbmsId is found!");
        }

        issueService.updateIssueStateByRdbmsId(State.Deleted.toString(), issueRdbmsId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);

    }

    @RolesAllowed("super_admin")
    @DeleteMapping("/delete/permanent/by/issue-id/{issueRdbmsId}")
    public ResponseEntity<Void> deleteIssuePermanentlyByRdbmsId(@RequestHeader("Authorization") String authorization, @PathVariable("issueRdbmsId") Long issueRdbmsId) {

        if (!issueService.exists(issueRdbmsId)) {
            throw new NotFoundException("No issue with this issueRdbmsId is found!");
        }

        if (!issueService.findIssueStateByRdbmsId(issueRdbmsId).equals(State.Deleted.toString())) {
            throw new BadRequestException("This issue can not be deleted permanently! Can be deleted temporarily.");
        }

        issueService.deleteIssueByRdbmsId(issueRdbmsId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @RolesAllowed("super_admin")
    @GetMapping("/find/by/issue-id/{issueRdbmsId}")
    public ResponseEntity<IssueResource> findIssueByRdbmsId(@RequestHeader("Authorization") String authorization, @PathVariable("issueRdbmsId") Long issueRdbmsId) {

        return new ResponseEntity<>(issueService.findIssueByRdbmsId(issueRdbmsId, authorization), HttpStatus.OK);

    }

    @RolesAllowed("super_admin")
    @GetMapping("/find/all/")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<IssueResource>> findAllIssues(@RequestHeader("Authorization") String authorization, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        Response response = issueService.findAllIssues(pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<IssueResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @GetMapping("/find/all/by/issue-type-id/{issueTypeRdbmsId}")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<IssueResource>> findAllIssuesByIssueTypeRdbmsId(@RequestHeader("Authorization") String authorization, @PathVariable("issueTypeRdbmsId") Short issueTypeRdbmsId, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        if (!issueService.issueTypeExists(issueTypeRdbmsId)) {
            throw new NotFoundException("No issue type with this issueTypeRdbmsId is found!");
        }

        Response response = issueService.findAllIssuesByIssueTypeRdbmsId(issueTypeRdbmsId, pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<IssueResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @GetMapping("/find/all/by/issue-type-id/{issueTypeRdbmsId}/and/issue-state/{issueState}")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<IssueResource>> findAllIssuesByIssueTypeRdbmsIdAndState(@RequestHeader("Authorization") String authorization, @PathVariable("issueTypeRdbmsId") Short issueTypeRdbmsId, @PathVariable("issueState") String issueState, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        if (issueState == null || (!issueState.equals(State.Active.toString()) && !issueState.equals(State.Inactive.toString()) &&
                !issueState.equals(State.Disabled.toString()) && !issueState.equals(State.Deleted.toString()))) {
            throw new BadRequestException("Invalid issueState value! Only Active, Inactive, Disabled Or Deleted value is allowed.");
        }

        if (!issueService.issueTypeExists(issueTypeRdbmsId)) {
            throw new NotFoundException("No issue type with this issueTypeRdbmsId is found!");
        }

        Response response = issueService.findAllIssuesByIssueTypeRdbmsIdAndState(issueTypeRdbmsId, issueState, pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<IssueResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("all_user")
    @GetMapping("/find/all/by/issue-reporter-id/")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<IssueResource>> findAllIssuesByIssueReporterAccountRdbmsId(@RequestHeader("Authorization") String authorization, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        Response response = issueService.findAllIssuesByIssueReporterAccountRdbmsId(keycloakApiService.findAuthenticatedUser(authorization).getAccountId(), pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<IssueResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @GetMapping("/find/all/by/issue-reporter-id/{issueReporterAccountRdbmsId}/and/issue-state/{issueState}")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<IssueResource>> findAllIssuesByIssueReporterAccountRdbmsIdAndState(@RequestHeader("Authorization") String authorization, @PathVariable("issueReporterAccountRdbmsId") Long issueReporterAccountRdbmsId, @PathVariable("issueState") String issueState, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        if (issueState == null || (!issueState.equals(State.Active.toString()) && !issueState.equals(State.Inactive.toString()) &&
                !issueState.equals(State.Disabled.toString()) && !issueState.equals(State.Deleted.toString()))) {
            throw new BadRequestException("Invalid issueState value! Only Active, Inactive, Disabled Or Deleted value is allowed.");
        }

        if (!issueService.accountExists(issueReporterAccountRdbmsId, authorization)) {
            throw new NotFoundException("No user account with this issueReporterAccountRdbmsId is found!");
        }

        Response response = issueService.findAllIssuesByIssueReporterAccountRdbmsIdAndState(issueReporterAccountRdbmsId, issueState, pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<IssueResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @GetMapping("/find/all/by/issue-state/{issueState}")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<IssueResource>> findAllIssuesByState(@RequestHeader("Authorization") String authorization, @PathVariable("issueState") String issueState, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        if (issueState == null || (!issueState.equals(State.Active.toString()) && !issueState.equals(State.Inactive.toString()) &&
                !issueState.equals(State.Disabled.toString()) && !issueState.equals(State.Deleted.toString()))) {
            throw new BadRequestException("Invalid issueState value! Only Active, Inactive, Disabled Or Deleted value is allowed.");
        }

        Response response = issueService.findAllIssuesByState(issueState, pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<IssueResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @PostMapping("/find/all/by/keyword/")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<IssueResource>> findAllIssuesByKeyword(@RequestHeader("Authorization") String authorization, @RequestParam("keyword") String keyword, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        if (!Helper.isValidKeyword(keyword)) {
            throw new BadRequestException("Invalid keyword!");
        }

        Response response = issueService.findAllIssuesByKeyword(keyword, pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<IssueResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @PostMapping("/find/all/by/multiple-ids/")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<IssueResource>> findAllIssuesByMultipleRdbmsIds(@RequestHeader("Authorization") String authorization, @RequestParam("multipleRdbmsIds") Set<Short> multipleRdbmsIds, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        Response response = issueService.findAllIssuesByMultipleRdbmsIds(multipleRdbmsIds, pageable, authorization);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<IssueResource>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @GetMapping("/count/all/")
    public ResponseEntity<Long> countAllIssues(@RequestHeader("Authorization") String authorization) {

        return new ResponseEntity<>(issueService.countTotalIssues(), HttpStatus.OK);

    }

    @RolesAllowed("super_admin")
    @GetMapping("/count/all/by/issue-type-id/{issueTypeRdbmsId}")
    public ResponseEntity<Long> countAllSchoolsByIssueTypeRdbmsId(@RequestHeader("Authorization") String authorization, @PathVariable("issueTypeRdbmsId") Short issueTypeRdbmsId) {

        if (!issueService.issueTypeExists(issueTypeRdbmsId)) {
            throw new NotFoundException("No issue type with this issueTypeRdbmsId is found!");
        }

        return new ResponseEntity<>(issueService.countTotalIssuesByIssueTypeRdbmsId(issueTypeRdbmsId), HttpStatus.OK);

    }

    @RolesAllowed("super_admin")
    @GetMapping("/count/all/by/issue-type-id/{issueTypeRdbmsId}/and/issue-state/{issueState}")
    public ResponseEntity<Long> countAllSchoolsByIssueTypeRdbmsIdAndState(@RequestHeader("Authorization") String authorization, @PathVariable("issueTypeRdbmsId") Short issueTypeRdbmsId, @PathVariable("issueState") String issueState) {

        if (issueState == null || (!issueState.equals(State.Active.toString()) && !issueState.equals(State.Inactive.toString()) &&
                !issueState.equals(State.Disabled.toString()) && !issueState.equals(State.Deleted.toString()))) {
            throw new BadRequestException("Invalid issueState value! Only Active, Inactive, Disabled Or Deleted value is allowed.");
        }

        if (!issueService.issueTypeExists(issueTypeRdbmsId)) {
            throw new NotFoundException("No issue type with this issueTypeRdbmsId is found!");
        }

        return new ResponseEntity<>(issueService.countTotalIssuesByIssueTypeRdbmsIdAndState(issueTypeRdbmsId, issueState), HttpStatus.OK);

    }

    @RolesAllowed("all_user")
    @GetMapping("/count/all/by/issue-reporter-id/")
    public ResponseEntity<Long> countAllSchoolsByIssueReporterAccountRdbmsId(@RequestHeader("Authorization") String authorization) {

        return new ResponseEntity<>(issueService.countTotalIssuesByIssueReporterAccountRdbmsId(keycloakApiService.findAuthenticatedUser(authorization).getAccountId()), HttpStatus.OK);

    }

    @RolesAllowed("super_admin")
    @GetMapping("/count/all/by/issue-reporter-id/{issueReporterAccountRdbmsId}/and/issue-state/{issueState}")
    public ResponseEntity<Long> countAllSchoolsByIssueReporterAccountRdbmsIdAndState(@RequestHeader("Authorization") String authorization, @PathVariable("issueTypeRdbmsId") Long issueReporterAccountRdbmsId, @PathVariable("issueState") String issueState) {

        if (issueState == null || (!issueState.equals(State.Active.toString()) && !issueState.equals(State.Inactive.toString()) &&
                !issueState.equals(State.Disabled.toString()) && !issueState.equals(State.Deleted.toString()))) {
            throw new BadRequestException("Invalid issueState value! Only Active, Inactive, Disabled Or Deleted value is allowed.");
        }

        if (!issueService.accountExists(issueReporterAccountRdbmsId, authorization)) {
            throw new NotFoundException("No user account with this issueReporterAccountRdbmsId is found!");
        }

        return new ResponseEntity<>(issueService.countTotalIssuesByIssueReporterAccountRdbmsIdAndState(issueReporterAccountRdbmsId, issueState), HttpStatus.OK);

    }

    @RolesAllowed("super_admin")
    @GetMapping("/count/all/by/issue-state/{issueState}")
    public ResponseEntity<Long> countAllSchoolsByState(@RequestHeader("Authorization") String authorization, @PathVariable("issueState") String issueState) {

        if (issueState == null || (!issueState.equals(State.Active.toString()) && !issueState.equals(State.Inactive.toString()) &&
                !issueState.equals(State.Disabled.toString()) && !issueState.equals(State.Deleted.toString()))) {
            throw new BadRequestException("Invalid issueState value! Only Active, Inactive, Disabled Or Deleted value is allowed.");
        }

        return new ResponseEntity<>(issueService.countTotalIssuesByState(issueState), HttpStatus.OK);

    }

}