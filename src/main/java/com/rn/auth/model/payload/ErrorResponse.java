package com.rn.auth.model.payload;

import java.util.Date;
import java.util.List;

public class ErrorResponse {
    private final Integer status;
    private final Date timestamp;
    private final String path;
    private final List<String> errors;




    public ErrorResponse(
        Integer status,
        Date timestamp,
        String path,
        List<String> errors
    ) {
        this.status = status;
        this.timestamp = timestamp;
        this.path = path;
        this.errors = errors;
    }




    public Integer getStatus() {
        return status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    public List<String> getErrors() {
        return errors;
    }
}
