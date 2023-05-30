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
        return route(GET(MAIN_PATH + "get-last-conversations"),chatService::getLastConversations)
                .andRoute(GET(MAIN_PATH + "get-conversations"),chatService::getConversations)
                .andRoute(GET(MAIN_PATH + "get-last-private-messages"),chatService::getLastPrivateMessages)
                .andRoute(GET(MAIN_PATH + "get-last-group-messages"),chatService::getLastGroupMessages)
                .andRoute(GET(MAIN_PATH + "get-group-members"),chatService::getGroupMembers)
                .andRoute(POST(MAIN_PATH + "push-private-message"),chatService::pushPrivateMessage)
                .andRoute(POST(MAIN_PATH + "push-group-message"),chatService::pushGroupMessage)
                .andRoute(POST(MAIN_PATH + "create-group"),chatService::createGroup)
                .andRoute(POST(MAIN_PATH + "add-group-members"),chatService::addGroupMembers)
                .andRoute(DELETE(MAIN_PATH + "remove-group"),chatService::removeGroup)
                .andRoute(DELETE(MAIN_PATH + "remove-group-members"),chatService::removeGroupMembers);
    }
}
