package com.simplenazmul.com.feedbackapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.simplenazmul.com.feedbackapi.helper.Helper;
import com.simplenazmul.com.feedbackapi.resource.AccountResource;

import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class MySQLApiServiceImpl implements MySQLApiService {

    Logger logger = LoggerFactory.getLogger(MySQLApiServiceImpl.class);

    WebClient accountApiClient;

    private String ACCOUNT_API_URL;

    @Autowired
    private Environment environment;

    @Bean
    public void initFeedBackApiMySQLApiServiceImpl() {

        this.ACCOUNT_API_URL = environment.getProperty("ACCOUNT_API_URL");

        logger.info("Account API URL: " + this.ACCOUNT_API_URL);

        this.accountApiClient = WebClient.builder().baseUrl(this.ACCOUNT_API_URL)
                .defaultCookie("cookieKey", "cookieValue")
                .defaultUriVariables(Collections.singletonMap("url", this.ACCOUNT_API_URL)).build();

    }


    @Override
    public List<AccountResource> findAccountInformationByMultipleRdbmsIds(Set<Long> userIds, ParameterizedTypeReference<List<AccountResource>> typeReference, String authorization) {
        String findAccountUrl = "/user/find/user-basic-info/by/multiple-ids/";

        return accountApiClient.post()
                .uri(findAccountUrl).header("Authorization", authorization)
                .body(Mono.just(userIds), HashSet.class).retrieve()
                .onStatus(HttpStatus::is4xxClientError, Helper::handle4xxAnd5xxError)
                .onStatus(HttpStatus::is5xxServerError, Helper::handle4xxAnd5xxError)
                .bodyToMono(typeReference).block();
    }

    @Override
    public Boolean doesAccountExist(Long accountRdbmsId, String authorization) {
        String findAccountUrl = "/account/does-account-exist/by/account-id/" + accountRdbmsId;

        return accountApiClient.get()
                .uri(findAccountUrl).header("Authorization", authorization).retrieve()
                .onStatus(HttpStatus::is4xxClientError, Helper::handle4xxAnd5xxError)
                .onStatus(HttpStatus::is5xxServerError, Helper::handle4xxAnd5xxError)
                .bodyToMono(Boolean.class).block();
    }

}