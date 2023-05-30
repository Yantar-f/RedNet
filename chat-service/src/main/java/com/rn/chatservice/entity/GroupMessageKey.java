package com.rn.chatservice.entity;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

@PrimaryKeyClass
public class GroupMessageKey implements Serializable {

    @PrimaryKeyColumn(
        name = "group_id",
        type = PrimaryKeyType.PARTITIONED)
    private String groupId;

    @PrimaryKeyColumn(
        name = "user_id",
        type = PrimaryKeyType.PARTITIONED)
    private String userId;

    @PrimaryKeyColumn(
        name = "message_id",
        ordering = Ordering.DESCENDING)
    private String messageId;

    public GroupMessageKey(
        String groupId,
        String userId,
        String messageId
    ) {
        this.groupId = groupId;
        this.userId = userId;
        this.messageId = messageId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessageId() {
        return messageId;
    }
}
