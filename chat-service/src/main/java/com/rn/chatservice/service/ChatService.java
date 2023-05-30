package com.rn.chatservice.service;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface ChatService {
    Mono<ServerResponse> getLastPrivateMessages(ServerRequest request);
    Mono<ServerResponse> pushPrivateMessage(ServerRequest request);
    Mono<ServerResponse> getLastGroupMessages(ServerRequest request);
    Mono<ServerResponse> pushGroupMessage(ServerRequest request);
    Mono<ServerResponse> getGroupMembers(ServerRequest request);
    Mono<ServerResponse> getConversations(ServerRequest request);
    Mono<ServerResponse> getLastConversations(ServerRequest request);
    Mono<ServerResponse> createGroup(ServerRequest request);
    Mono<ServerResponse> addGroupMembers(ServerRequest request);
    Mono<ServerResponse> removeGroupMembers(ServerRequest request);
    Mono<ServerResponse> removeGroup(ServerRequest request);
}
