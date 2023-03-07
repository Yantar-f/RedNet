package com.rn.auth.payload;

import java.util.Date;
import java.util.List;

public class ErrorResponseBody {
    private final Integer status;
    private final Date timestamp;
    private final String path;
    private final List<String> messages;




    public ErrorResponseBody(
        Integer status,
        Date timestamp,
        String path,
        List<String> messages
    ) {
        this.status = status;
        this.timestamp = timestamp;
        this.path = path;
        this.messages = messages;
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

    public List<String> getMessages() {
        return messages;
    }
}
