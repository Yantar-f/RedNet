package com.rn.chatservice.payload;

import java.util.Date;

public class PushGroupMessageResponseBody {
    private final String messageId;
    private final Date timestamp;

    public PushGroupMessageResponseBody(String messageId, Date timestamp) {
        this.messageId = messageId;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
