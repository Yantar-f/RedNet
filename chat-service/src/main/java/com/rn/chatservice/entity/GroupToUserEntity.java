package com.rn.chatservice.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

@Table("groups_to_users")
public class GroupToUserEntity {

    @PrimaryKeyColumn(
        name = "group_id",
        type = PARTITIONED)
    private final String groupId;

    @PrimaryKeyColumn(
        name = "user_id",
        type = CLUSTERED)
    private final String userId;

    public GroupToUserEntity(String groupId, String userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getUserId() {
        return userId;
    }
}
