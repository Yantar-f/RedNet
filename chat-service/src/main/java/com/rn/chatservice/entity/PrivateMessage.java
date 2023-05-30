package com.rn.chatservice.entity;

import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

@Table("private_messages")
public class PrivateMessage {
    private PrivateMessageKey key;
    private Date timestamp;
    private String message;

    public PrivateMessage(
        PrivateMessageKey key,
        String message,
        Date timestamp
    ) {
        this.key = key;
        this.message = message;
        this.timestamp = timestamp;
    }

    public PrivateMessageKey getKey() {
        return key;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}
