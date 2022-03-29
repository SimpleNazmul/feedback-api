package com.simplenazmul.com.feedbackapi.resource;

import java.util.Objects;

public class KeycloakUserResource {

    public String sub;

    public Long accountId;

    public Boolean email_verified;

    public String accountType;

    public String name;

    public String preferred_username;

    public String given_name;

    public String family_name;

    public String email;

    public KeycloakUserResource() {
    }

    public KeycloakUserResource(String sub, Long accountId, Boolean email_verified, String accountType, String name, String preferred_username, String given_name, String family_name, String email) {
        this.sub = sub;
        this.accountId = accountId;
        this.email_verified = email_verified;
        this.accountType = accountType;
        this.name = name;
        this.preferred_username = preferred_username;
        this.given_name = given_name;
        this.family_name = family_name;
        this.email = email;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Boolean getEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(Boolean email_verified) {
        this.email_verified = email_verified;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreferred_username() {
        return preferred_username;
    }

    public void setPreferred_username(String preferred_username) {
        this.preferred_username = preferred_username;
    }

    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeycloakUserResource that = (KeycloakUserResource) o;
        return Objects.equals(sub, that.sub);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sub);
    }

    @Override
    public String toString() {
        return "KeycloakUserResource{" +
                "sub='" + sub + '\'' +
                ", accountId=" + accountId +
                ", email_verified=" + email_verified +
                ", accountType='" + accountType + '\'' +
                ", name='" + name + '\'' +
                ", preferred_username='" + preferred_username + '\'' +
                ", given_name='" + given_name + '\'' +
                ", family_name='" + family_name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}