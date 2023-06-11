package com.rn.chatservice.service;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface ChatService {
    Mono<ServerResponse> addConversationMembers(ServerRequest request);
    Mono<ServerResponse> editMessage(ServerRequest request);

    Mono<ServerResponse> getLastMessages(ServerRequest request);

    Mono<ServerResponse> getConversationMembers(ServerRequest request);

    Mono<ServerResponse> getConversationPreloadedList(ServerRequest request);

    Mono<ServerResponse> createConversation(ServerRequest request);

    Mono<ServerResponse> pushMessage(ServerRequest request);

    Mono<ServerResponse> removeConversationMembers(ServerRequest request);

    Mono<ServerResponse> removeConversationHistory(ServerRequest request);
    Mono<ServerResponse> removeMessage(ServerRequest request);

    Mono<ServerResponse> getConversationMessages(ServerRequest request);

    Mono<ServerResponse> editConversationTitle(ServerRequest request);
}
