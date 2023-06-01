package com.rn.chatservice.repository;

import com.rn.chatservice.entity.UserToGroupEntity;
import com.rn.chatservice.entity.UserToGroupKey;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface UserToGroupRepository extends ReactiveCassandraRepository<UserToGroupEntity, UserToGroupKey> {
    Flux<UserToGroupEntity> findAllByUserId(String userId);
}
