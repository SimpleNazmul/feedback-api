package com.simplenazmul.com.feedbackapi.helper;

import java.sql.Timestamp;
import java.util.Objects;

public class Error {

    Timestamp timestamp;

    Short status;

    String error;

    String message;

    String path;

    public Error() {
    }

    public Error(Timestamp timestamp, Short status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public Short getStatus() {
        return this.status;
    }

    public String getError() {
        return this.error;
    }

    public String getMessage() {
        return this.message;
    }

    public String getPath() {
        return this.path;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Error that = (Error) o;
        return timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }

    @Override
    public String toString() {
        return "Error{" +
                "timestamp=" + timestamp +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
    
}