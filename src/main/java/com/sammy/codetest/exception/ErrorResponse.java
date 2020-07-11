package com.sammy.codetest.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "timestamp",
        "error",
        "status",
        "statusCode"
})
public class ErrorResponse {

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    @JsonProperty("error")
    private String error;
    @JsonProperty("status")
    private HttpStatus status;
    @JsonProperty("statusCode")
    private Integer statusCode;

    /**
     * No args constructor for use in serialization
     *
     */
    public ErrorResponse() {
    }

    public ErrorResponse(String error, LocalDateTime timestamp, HttpStatus status) {
        this.error = error;
        this.timestamp = timestamp;
        this.status = status;
        this.statusCode = status.value();
    }

    @JsonProperty("timestamp")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("error")
    public String getError() {
        return error;
    }

    @JsonProperty("error")
    public void setError(String error) {
        this.error = error;
    }

    @JsonProperty("status")
    public HttpStatus getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    @JsonProperty("statusCode")
    public Integer getStatusCode() {
        return statusCode;
    }

    @JsonProperty("statusCode")
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
