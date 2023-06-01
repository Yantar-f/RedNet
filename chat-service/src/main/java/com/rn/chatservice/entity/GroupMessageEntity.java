package com.rn.chatservice.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

@Table("group_messages")
public class GroupMessageEntity {

    @PrimaryKey
    private final GroupMessageKey key;

    @Column("timestamp")
    private final Date timestamp;

    @Column("message")
    private String message;

    public GroupMessageEntity(
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

    public void setMessage(String editedMessage){
        this.message = editedMessage;
    }
}
