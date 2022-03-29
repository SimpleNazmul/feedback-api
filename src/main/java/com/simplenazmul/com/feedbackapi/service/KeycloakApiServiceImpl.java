package com.simplenazmul.com.feedbackapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.simplenazmul.com.feedbackapi.exception.BadRequestException;
import com.simplenazmul.com.feedbackapi.resource.KeycloakUserResource;

import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

@Service
@Transactional
public class KeycloakApiServiceImpl implements KeycloakApiService {

    Logger logger = LoggerFactory.getLogger(KeycloakApiServiceImpl.class);

    WebClient keycloakClient;
    private String realm;

    @Autowired
    private Environment environment;

    @Bean
    public void KeycloakApiServiceImplClass() {

        this.realm = environment.getProperty("keycloak.realm");
        String authServerUrl = environment.getProperty("keycloak.auth-server-url");

        keycloakClient = WebClient.builder().baseUrl(authServerUrl + "/realms")
                .defaultCookie("cookieKey", "cookieValue")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", authServerUrl)).build();

    }

    @Override
    public KeycloakUserResource findAuthenticatedUser(String authorization) {

        String customUrl = "/" + this.realm + "/protocol/openid-connect/userinfo";

        return keycloakClient.get().uri(customUrl)
                .header("Authorization", authorization).retrieve()
                .onStatus(HttpStatus::is4xxClientError, this::handle4xxAnd5xxError)
                .onStatus(HttpStatus::is5xxServerError, this::handle4xxAnd5xxError)
                .bodyToMono(KeycloakUserResource.class).block();

    }




    // ---------------Important Methods----------------- //
    private Mono<? extends Throwable> handle4xxAnd5xxError(ClientResponse clientResponse) {
        Mono<String> errorMessage = clientResponse.bodyToMono(String.class);
        logger.info("ERROR :" + errorMessage);

        return errorMessage.flatMap((message) -> {
            logger.error("Status Code :" + clientResponse.statusCode() + ". And Error is : " + message);

            throw new BadRequestException(
                    "Status Code :" + clientResponse.statusCode() + ". And Error is : " + message);

        });

    }

    public static String encodeURLComponent(String component) {
        return URLEncoder.encode(component, StandardCharsets.UTF_8);
    }

    public static String queryStringFromPageable(Pageable p) {
        StringBuilder ans = new StringBuilder();
        ans.append("page=");
        ans.append(encodeURLComponent(p.getPageNumber() + ""));
        ans.append("&size=");
        ans.append(encodeURLComponent(p.getPageSize() + ""));

        // No sorting
        p.getSort();

        // Sorting is specified
        for (Sort.Order o : p.getSort()) {
            ans.append("&sort=");
            ans.append(encodeURLComponent(o.getProperty()));
            ans.append(",");
            ans.append(encodeURLComponent(o.getDirection().name()));
        }

        return ans.toString();
    }

    public MultiValueMap<String, HttpEntity<?>> multiValueMap(Map<String, Object> objects) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        for (Map.Entry<String, Object> object : objects.entrySet()) {
            builder.part(object.getKey(), object.getValue());
        }
        return builder.build();
    }

}