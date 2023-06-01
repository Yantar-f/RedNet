package com.rn.chatservice.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

@Table("users_to_groups")
public class UserToGroupEntity {

    @PrimaryKeyColumn(
        name = "user_id",
        type = PARTITIONED)
    private final String userId;

    @PrimaryKeyColumn(
        name = "group_id",
        type = CLUSTERED)
    private final String groupId;

    public UserToGroupEntity(String userId, String groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public String getGroupId() {
        return groupId;
    }
}
