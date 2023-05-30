package com.rn.chatservice.payload;

import java.util.Date;

public class PushPrivateMessageResponseBody {
    private final String messageId;
    private final Date timestamp;

    public PushPrivateMessageResponseBody(String messageId, Date timestamp) {
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
