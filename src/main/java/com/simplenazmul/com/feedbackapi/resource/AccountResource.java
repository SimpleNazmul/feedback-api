package com.simplenazmul.com.feedbackapi.resource;

import java.sql.Timestamp;
import java.util.Objects;

public class AccountResource {

    private Long accountId;

    private String firstName;

    private String lastName;

    private String userUrlName;

    private String gender;

    private String profilePicLink;

    private byte userLevel;

    private String accountType;

    private String accountState;

    private Timestamp accountCreatedTimestamp;

    private Timestamp accountLatestUpdatedTimestamp;

    public AccountResource() {
    }

    public AccountResource(Long accountId, String firstName, String lastName, String userUrlName, String gender, String profilePicLink, byte userLevel, String accountType, String accountState, Timestamp accountCreatedTimestamp, Timestamp accountLatestUpdatedTimestamp) {
        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userUrlName = userUrlName;
        this.gender = gender;
        this.profilePicLink = profilePicLink;
        this.userLevel = userLevel;
        this.accountType = accountType;
        this.accountState = accountState;
        this.accountCreatedTimestamp = accountCreatedTimestamp;
        this.accountLatestUpdatedTimestamp = accountLatestUpdatedTimestamp;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserUrlName() {
        return userUrlName;
    }

    public void setUserUrlName(String userUrlName) {
        this.userUrlName = userUrlName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePicLink() {
        return profilePicLink;
    }

    public void setProfilePicLink(String profilePicLink) {
        this.profilePicLink = profilePicLink;
    }

    public byte getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(byte userLevel) {
        this.userLevel = userLevel;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountState() {
        return accountState;
    }

    public void setAccountState(String accountState) {
        this.accountState = accountState;
    }

    public Timestamp getAccountCreatedTimestamp() {
        return accountCreatedTimestamp;
    }

    public void setAccountCreatedTimestamp(Timestamp accountCreatedTimestamp) {
        this.accountCreatedTimestamp = accountCreatedTimestamp;
    }

    public Timestamp getAccountLatestUpdatedTimestamp() {
        return accountLatestUpdatedTimestamp;
    }

    public void setAccountLatestUpdatedTimestamp(Timestamp accountLatestUpdatedTimestamp) {
        this.accountLatestUpdatedTimestamp = accountLatestUpdatedTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountResource that = (AccountResource) o;
        return Objects.equals(accountId, that.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId);
    }

    @Override
    public String toString() {
        return "AccountResource{" +
                "accountId=" + accountId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userUrlName='" + userUrlName + '\'' +
                ", gender='" + gender + '\'' +
                ", profilePicLink='" + profilePicLink + '\'' +
                ", userLevel=" + userLevel +
                ", accountType='" + accountType + '\'' +
                ", accountState='" + accountState + '\'' +
                ", accountCreatedTimestamp=" + accountCreatedTimestamp +
                ", accountLatestUpdatedTimestamp=" + accountLatestUpdatedTimestamp +
                '}';
    }

}