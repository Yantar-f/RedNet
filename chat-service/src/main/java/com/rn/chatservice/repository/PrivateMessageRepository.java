package com.rn.chatservice.repository;

import com.rn.chatservice.entity.PrivateMessage;
import com.rn.chatservice.entity.PrivateMessageKey;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivateMessageRepository extends ReactiveCassandraRepository<PrivateMessage, PrivateMessageKey> {
}
