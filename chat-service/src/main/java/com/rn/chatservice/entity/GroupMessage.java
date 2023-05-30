package com.rn.chatservice.entity;

import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

@Table("group_messages")
public class GroupMessage {
    private GroupMessageKey key;
    private Date timestamp;
    private String message;

    public GroupMessage(
        GroupMessageKey key,
        Date timestamp,
        String message
    ) {
        this.key = key;
        this.timestamp = timestamp;
        this.message = message;
    }

    public GroupMessageKey getKey() {
        return key;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}
