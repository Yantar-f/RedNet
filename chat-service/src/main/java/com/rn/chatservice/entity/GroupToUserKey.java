package com.rn.chatservice.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;

@PrimaryKeyClass
public class GroupToUserKey {

    @PrimaryKey
    private final GroupToUserKey key;

    public GroupToUserKey(GroupToUserKey key) {
        this.key = key;
    }

    public GroupToUserKey getKey() {
        return key;
    }
}
