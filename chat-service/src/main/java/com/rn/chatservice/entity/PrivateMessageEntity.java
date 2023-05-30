package com.rn.chatservice.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

@Table("private_messages")
public class PrivateMessageEntity {

    @PrimaryKey
    private PrivateMessageKey key;

    @Column("timestamp")
    private Date timestamp;

    @Column("message")
    private String message;

    public PrivateMessageEntity(
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
