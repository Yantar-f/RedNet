package com.rn.apigateway.controller;

import com.rn.apigateway.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class GatewayController {
    private final GatewayService gatewayService;




    @Autowired
    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }




    @GetMapping("/api/signout")
    public Mono<ResponseEntity<String>> signOut(ServerHttpRequest request) {
        return Mono.just(gatewayService.signOut(request));
    }

}
