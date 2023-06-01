package com.rn.chatservice.config;

import com.rn.chatservice.service.ChatService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouteConfig {
    private final ChatService chatService;
    public static final String MAIN_PATH = "/api/chat";

    public RouteConfig(ChatService chatService) {
        this.chatService = chatService;
    }

    @Bean
    public RouterFunction<ServerResponse> routes(){
        return route(POST(MAIN_PATH + "add-conversation-members"),chatService::addConversationMembers)//(conversationKey, userId[])
            .andRoute(GET(MAIN_PATH + "edit-message"),chatService::editMessage)//(messageId,conversationKey)
            .andRoute(GET(MAIN_PATH + "get-last-conversations"),chatService::getLastConversations)//(lastConversationKey)
            .andRoute(GET(MAIN_PATH + "get-conversation-members"),chatService::getConversationMembers)//(conversationKey)
            .andRoute(POST(MAIN_PATH + "push-message"),chatService::pushMessage)//(conversationKey,message)
            .andRoute(POST(MAIN_PATH + "create-conversation"),chatService::createConversation)//(userId[], title)
            .andRoute(DELETE(MAIN_PATH + "remove-conversation-history"),chatService::removeConversationHistory)//(conversationKey)
            .andRoute(DELETE(MAIN_PATH + "remove-conversation-members"),chatService::removeConversationMembers)//(conversationKey,userId[])
            .andRoute(DELETE(MAIN_PATH + "remove-message"),chatService::removeMessage);//(conversationKey, messageId)
    }
}
