package com.rn.chatservice.service.impl;

import com.rn.chatservice.service.ChatService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class ChatServiceImpl implements ChatService {

    public Mono<ServerResponse> addConversationMembers(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> editMessage(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> getLastMessages(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> getConversationMembers(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> getLastConversations(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> createConversation(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> pushMessage(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> removeConversationMembers(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> removeConversationHistory(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> removeMessage(ServerRequest request) {
        return null;
    }
}
