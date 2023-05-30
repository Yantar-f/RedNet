package com.rn.chatservice.repository;

import com.rn.chatservice.entity.GroupMessageEntity;
import com.rn.chatservice.entity.GroupMessageKey;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMessageRepository extends ReactiveCassandraRepository<GroupMessageEntity, GroupMessageKey> {
}
