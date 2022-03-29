package com.simplenazmul.com.feedbackapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.simplenazmul.com.feedbackapi.entity.IssueType;
import com.simplenazmul.com.feedbackapi.exception.BadRequestException;
import com.simplenazmul.com.feedbackapi.exception.ConflictException;
import com.simplenazmul.com.feedbackapi.exception.NotFoundException;
import com.simplenazmul.com.feedbackapi.exception.UnprocessableEntityException;
import com.simplenazmul.com.feedbackapi.helper.Helper;
import com.simplenazmul.com.feedbackapi.helper.Response;
import com.simplenazmul.com.feedbackapi.helper.State;
import com.simplenazmul.com.feedbackapi.service.IssueTypeService;
import com.simplenazmul.com.feedbackapi.service.KeycloakApiService;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/feedback-api/v1/issue-types")
@CrossOrigin(origins = { "https://localhost:2000"},
        allowCredentials = "true", maxAge = 3600, allowedHeaders = "*", exposedHeaders = {"X-Total-Count", "first", "last", "next", "prev"},
        methods = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.GET, RequestMethod.OPTIONS })
public class IssueTypeController {

    @Autowired
    private IssueTypeService issueTypeService;


    private static final int MAX_PAGE_SIZE = 20;

    @RolesAllowed("super_admin")
    @PostMapping("/save/")
    public ResponseEntity<IssueType> saveIssueType(@RequestHeader("Authorization") String authorization, @RequestParam("issueTypeName") String issueTypeNameString, @RequestParam("issueTypeUrlName") String issueTypeUrlNameString) {

        if (issueTypeNameString == null || issueTypeNameString.isBlank() ||
                issueTypeUrlNameString == null || issueTypeUrlNameString.isBlank()) {
            throw new UnprocessableEntityException("issueTypeName, issueTypeUrlName fields are required!");
        }

        String issueTypeName = issueTypeNameString.trim(), issueTypeUrlName = Helper.toSlugCase(issueTypeUrlNameString.trim());

        if (issueTypeName.length() > 50) {
            throw new UnprocessableEntityException("issueTypeName must be less than or equal to 50 characters!");
        }

        if (issueTypeUrlName.length() > 50) {
            throw new UnprocessableEntityException("issueTypeUrlName must be less than or equal to 50 characters!");
        }

        if (issueTypeService.nameExists(issueTypeName)) {
            throw new ConflictException("issueTypeName already exists!");
        }

        if (issueTypeService.urlNameExists(issueTypeUrlName)) {
            throw new ConflictException("issueTypeUrlName already exists!");
        }

        return new ResponseEntity<>(issueTypeService.saveIssueType(issueTypeName, issueTypeUrlName), HttpStatus.CREATED);

    }

    @RolesAllowed("super_admin")
    @PutMapping("/update/issue-type-name/by/issue-type-id/{issueTypeRdbmsId}")
    public ResponseEntity<Void> updateIssueTypeNameByRdbmsId(@RequestHeader("Authorization") String authorization, @PathVariable("issueTypeRdbmsId") Short issueTypeRdbmsId, @RequestParam("issueTypeName") String issueTypeNameString) {

        if (issueTypeNameString == null || issueTypeNameString.isBlank()) {
            throw new UnprocessableEntityException("issueTypeName field is required!");
        }

        String issueTypeName = issueTypeNameString.trim();

        if (issueTypeName.length() > 50) {
            throw new UnprocessableEntityException("issueTypeName must be less than or equal to 50 characters!");
        }

        if (!issueTypeService.exists(issueTypeRdbmsId)) {
            throw new NotFoundException("No issue type with this issueTypeRdbmsId is found!");
        }

        if (issueTypeService.nameExists(issueTypeName)) {
            throw new ConflictException("issueTypeName already exists!");
        }

        issueTypeService.updateIssueTypeNameByRdbmsId(issueTypeName, issueTypeRdbmsId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);

    }

    @RolesAllowed("super_admin")
    @PutMapping("/update/issue-type-url-name/by/issue-type-id/{issueTypeRdbmsId}")
    public ResponseEntity<Void> updateIssueTypeUrlNameByRdbmsId(@RequestHeader("Authorization") String authorization, @PathVariable("issueTypeRdbmsId") Short issueTypeRdbmsId, @RequestParam("issueTypeUrlName") String issueTypeUrlNameString) {

        if (issueTypeUrlNameString == null || issueTypeUrlNameString.isBlank()) {
            throw new UnprocessableEntityException("issueTypeUrlName field is required!");
        }

        String issueTypeUrlName = Helper.toSlugCase(issueTypeUrlNameString.trim());

        if (issueTypeUrlName.length() > 50) {
            throw new UnprocessableEntityException("issueTypeUrlName must be less than or equal to 50 characters!");
        }

        if (!issueTypeService.exists(issueTypeRdbmsId)) {
            throw new NotFoundException("No issue type with this issueTypeRdbmsId is found!");
        }

        if (issueTypeService.urlNameExists(issueTypeUrlName)) {
            throw new ConflictException("issueTypeUrlName already exists!");
        }

        issueTypeService.updateIssueTypeUrlNameByRdbmsId(issueTypeUrlName, issueTypeRdbmsId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);

    }

    @RolesAllowed("super_admin")
    @PutMapping("/update/issue-type-state/by/issue-type-id/{issueTypeRdbmsId}")
    public ResponseEntity<Void> updateIssueTypeStateByRdbmsId(@RequestHeader("Authorization") String authorization, @PathVariable("issueTypeRdbmsId") Short issueTypeRdbmsId, @RequestParam("issueTypeState") String issueTypeState) {

        if (issueTypeState == null || (!issueTypeState.equals(State.Active.toString()) &&
                !issueTypeState.equals(State.Disabled.toString()))) {
            throw new UnprocessableEntityException("Only 'Active', 'Inactive', 'Disabled' values are allowed as issueTypeState!");
        }

        if (!issueTypeService.exists(issueTypeRdbmsId)) {
            throw new NotFoundException("No issue type with this issueTypeRdbmsId is found!");
        }

        issueTypeService.updateIssueTypeStateByRdbmsId(issueTypeState, issueTypeRdbmsId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);

    }

    @RolesAllowed("super_admin")
    @DeleteMapping("/delete/temporary/by/issue-type-id/{issueTypeRdbmsId}")
    public ResponseEntity<Void> deleteIssueTypeTemporarilyByRdbmsId(@RequestHeader("Authorization") String authorization, @PathVariable("issueTypeRdbmsId") Short issueTypeRdbmsId) {

        if (!issueTypeService.exists(issueTypeRdbmsId)) {
            throw new NotFoundException("No issue type with this issueTypeRdbmsId is found!");
        }

        issueTypeService.updateIssueTypeStateByRdbmsId(State.Deleted.toString(), issueTypeRdbmsId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);

    }

    @RolesAllowed("super_admin")
    @DeleteMapping("/delete/permanent/by/issue-type-id/{issueTypeRdbmsId}")
    public ResponseEntity<Void> deleteIssueTypePermanentlyByRdbmsId(@RequestHeader("Authorization") String authorization, @PathVariable("issueTypeRdbmsId") Short issueTypeRdbmsId) {

        if (!issueTypeService.exists(issueTypeRdbmsId)) {
            throw new NotFoundException("No issue type with this issueTypeRdbmsId is found!");
        }

        if (!issueTypeService.findIssueTypeStateByRdbmsId(issueTypeRdbmsId).equals(State.Deleted.toString())) {
            throw new BadRequestException("This issue type can not be deleted permanently! Can be deleted temporarily.");
        }

        issueTypeService.deleteIssueTypeByRdbmsId(issueTypeRdbmsId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @RolesAllowed("super_admin")
    @GetMapping("/find/by/issue-type-id/{issueTypeRdbmsId}")
    public ResponseEntity<IssueType> findIssueTypeByRdbmsId(@RequestHeader("Authorization") String authorization, @PathVariable("issueTypeRdbmsId") Short issueTypeRdbmsId) {

        return new ResponseEntity<>(issueTypeService.findIssueTypeByRdbmsId(issueTypeRdbmsId), HttpStatus.OK);

    }

    @RolesAllowed("super_admin")
    @GetMapping("/find/by/issue-type-name/{issueTypeName}")
    public ResponseEntity<IssueType> findIssueTypeByName(@RequestHeader("Authorization") String authorization, @PathVariable("issueTypeName") String issueTypeName) {

        return new ResponseEntity<>(issueTypeService.findIssueTypeByName(issueTypeName), HttpStatus.OK);

    }

    @RolesAllowed("super_admin")
    @GetMapping("/find/by/issue-type-url-name/{issueTypeUrlName}")
    public ResponseEntity<IssueType> findIssueTypeByUrlName(@RequestHeader("Authorization") String authorization, @PathVariable("issueTypeUrlName") String issueTypeUrlName) {

        return new ResponseEntity<>(issueTypeService.findIssueTypeByUrlName(issueTypeUrlName), HttpStatus.OK);

    }

    @RolesAllowed("super_admin")
    @GetMapping("/find/all/")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<IssueType>> findAllIssueTypes(@RequestHeader("Authorization") String authorization, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        Response response = issueTypeService.findAllIssueTypes(pageable);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<IssueType>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("all_user")
    @GetMapping("/find/all/by/issue-type-state/{issueTypeState}")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<IssueType>> findAllIssueTypesByState(@RequestHeader("Authorization") String authorization, @PathVariable("issueTypeState") String issueTypeState, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        if (issueTypeState == null || (!issueTypeState.equals(State.Active.toString()) && !issueTypeState.equals(State.Inactive.toString()) &&
                !issueTypeState.equals(State.Disabled.toString()) && !issueTypeState.equals(State.Deleted.toString()))) {
            throw new BadRequestException("Invalid issueTypeState value! Only Active, Inactive, Disabled Or Deleted value is allowed.");
        }

        Response response = issueTypeService.findAllIssueTypesByState(issueTypeState, pageable);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<IssueType>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @PostMapping("/find/all/by/keyword/")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<IssueType>> findAllIssueTypesByKeyword(@RequestHeader("Authorization") String authorization, @RequestParam("keyword") String keyword, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        if (!Helper.isValidKeyword(keyword)) {
            throw new BadRequestException("Invalid keyword!");
        }

        Response response = issueTypeService.findAllIssueTypesByKeyword(keyword, pageable);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<IssueType>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @PostMapping("/find/all/by/multiple-ids/")
    @SuppressWarnings("unchecked") // casting is typesafe
    public ResponseEntity<List<IssueType>> findAllIssueTypesByMultipleRdbmsIds(@RequestHeader("Authorization") String authorization, @RequestParam("multipleRdbmsIds") Set<Short> multipleRdbmsIds, @PageableDefault(size = MAX_PAGE_SIZE) Pageable pageable) {

        Response response = issueTypeService.findAllIssueTypesByMultipleRdbmsIds(multipleRdbmsIds, pageable);

        if (response.getContents() == null && response.getHttpHeaders() == null) {
            return new ResponseEntity<>(response.getHttpStatusCode());
        }

        return new ResponseEntity<>((List<IssueType>) response.getContents(), response.getHttpHeaders(), response.getHttpStatusCode());

    }

    @RolesAllowed("super_admin")
    @GetMapping("/count/all/")
    public ResponseEntity<Long> countAllIssueTypes(@RequestHeader("Authorization") String authorization) {

        return new ResponseEntity<>(issueTypeService.countTotalIssueTypes(), HttpStatus.OK);

    }

    @RolesAllowed("super_admin")
    @GetMapping("/count/all/by/issue-type-state/{issueTypeState}")
    public ResponseEntity<Short> countAllSchoolsByState(@RequestHeader("Authorization") String authorization, @PathVariable("issueTypeState") String issueTypeState) {

        if (issueTypeState == null || (!issueTypeState.equals(State.Active.toString()) && !issueTypeState.equals(State.Inactive.toString()) &&
                !issueTypeState.equals(State.Disabled.toString()) && !issueTypeState.equals(State.Deleted.toString()))) {
            throw new BadRequestException("Invalid issueTypeState value! Only Active, Inactive, Disabled Or Deleted value is allowed.");
        }

        return new ResponseEntity<>(issueTypeService.countTotalIssueTypesByState(issueTypeState), HttpStatus.OK);

    }

}