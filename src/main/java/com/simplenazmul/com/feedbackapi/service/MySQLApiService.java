package com.simplenazmul.com.feedbackapi.service;

import org.springframework.core.ParameterizedTypeReference;

import com.simplenazmul.com.feedbackapi.resource.AccountResource;

import java.util.List;
import java.util.Set;

public interface MySQLApiService {

    List<AccountResource> findAccountInformationByMultipleRdbmsIds(Set<Long> userIds, ParameterizedTypeReference<List<AccountResource>> typeReference, String authorization);
    Boolean doesAccountExist(Long accountRdbmsId, String authorization);

}