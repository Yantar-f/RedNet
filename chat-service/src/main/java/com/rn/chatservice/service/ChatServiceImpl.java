package com.rn.chatservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class ChatServiceImpl implements ChatService {

    public Mono<ServerResponse> getLastMessages(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> pushMessage(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> getLastGroupMessages(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> pushGroupMessage(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> getGroupMembers(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> getConversations(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> getLastConversations(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> createGroup(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> addGroupMembers(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> removeGroupMembers(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> removeGroup(ServerRequest request) {
        return null;
    }
}
