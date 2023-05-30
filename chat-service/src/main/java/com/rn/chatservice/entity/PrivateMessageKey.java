package com.rn.chatservice.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

import static org.springframework.data.cassandra.core.cql.Ordering.DESCENDING;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

@PrimaryKeyClass
public class PrivateMessageKey implements Serializable {

    @PrimaryKeyColumn(
        name = "user_from_id",
        type = PARTITIONED)
    private String userFromId;

    @PrimaryKeyColumn(
        name = "user_to_id",
        type = PARTITIONED)
    private String userToId;

    @PrimaryKeyColumn(
        name = "message_id",
        type = CLUSTERED,
        ordering = DESCENDING)
    private String messageId;

    public PrivateMessageKey(
        String userFromId,
        String userToId,
        String messageId
    ) {
        this.userFromId = userFromId;
        this.userToId = userToId;
        this.messageId = messageId;
    }

    public String getUserFromId() {
        return userFromId;
    }

    public String getUserToId() {
        return userToId;
    }

    public String getMessageId() {
        return messageId;
    }
}
