package com.rn.chatservice.repository;

import com.rn.chatservice.entity.GroupMessage;
import com.rn.chatservice.entity.GroupMessageKey;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMessageRepository extends ReactiveCassandraRepository<GroupMessage, GroupMessageKey> {
}
