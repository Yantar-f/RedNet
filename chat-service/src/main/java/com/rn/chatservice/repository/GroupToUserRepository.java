package com.rn.chatservice.repository;

import com.rn.chatservice.entity.GroupToUserEntity;
import com.rn.chatservice.entity.GroupToUserKey;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface GroupToUserRepository extends ReactiveCassandraRepository<GroupToUserEntity, GroupToUserKey> {
    Flux<GroupToUserEntity> findAllByGroupId(String groupId);
}
