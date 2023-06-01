package com.rn.chatservice.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;

@PrimaryKeyClass
public class UserToGroupKey {

    @PrimaryKey
    private final UserToGroupKey key;

    public UserToGroupKey(UserToGroupKey key) {
        this.key = key;
    }

    public UserToGroupKey getKey() {
        return key;
    }
}
