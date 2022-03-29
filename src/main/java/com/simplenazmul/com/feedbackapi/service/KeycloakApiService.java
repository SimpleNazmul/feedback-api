package com.simplenazmul.com.feedbackapi.service;

import com.simplenazmul.com.feedbackapi.resource.KeycloakUserResource;

public interface KeycloakApiService {

    KeycloakUserResource findAuthenticatedUser(String authorization);

//    Response createUser(UserRepresentation userRepresentation);
//
//    Response updateUser(UserRepresentation userRepresentation);
//    void resetPassword(CredentialRepresentation credentialRepresentation);
//    void assignRealmRoleToUser(String userId, List<RoleRepresentation> roleRepresentations);
//
//    Response deleteUserById(String userId);
//
//    RoleRepresentation findRoleByRoleName(String roleName);
//    UserRepresentation findByUserId(String userId);
//    List<UserRepresentation> findAllUsers();
//
//    AccessTokenResponse getAccessToken(String username, String password);
//    AccessTokenResponse getTokenByRefreshToken(String refreshToken);
//    void logout(String userId);
//
//    Integer countTotalUser();

}