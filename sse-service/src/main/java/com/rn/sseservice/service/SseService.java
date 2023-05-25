package com.rn.sseservice.service;

import com.rn.sseservice.model.SubscriptionData;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {
    Map<UUID, SubscriptionData> subscriptions = new ConcurrentHashMap<>();
    private final String accessTokenCookieName;
    private final JwtParser jwtParser;

    public SseService(
        @Value ("${RedNet.app.accessTokenCookieName}") String accessTokenCookieName,
        @Value ("${RedNet.app.jwt.secretKey}") String jwtSecretKey
    ) {
        this.accessTokenCookieName = accessTokenCookieName;
        this.jwtParser = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey)))
            .build();
    }

    public Mono<ServerResponse> subscribe(ServerRequest request){
        HttpCookie accessTokenCookie = request.cookies().getFirst(accessTokenCookieName);
        if (accessTokenCookie == null) return ServerResponse.badRequest().build();

        String userId = jwtParser
            .parseClaimsJws(accessTokenCookie.getValue())
            .getBody()
            .getSubject();

        return ServerResponse.ok()
            .contentType(MediaType.TEXT_EVENT_STREAM)
            .body(BodyInserters.fromServerSentEvents(Flux.create(fluxSink -> {
                UUID uuid = UUID.randomUUID();
                SubscriptionData subscriptionData = new SubscriptionData(userId, fluxSink);

                subscriptions.put(uuid, subscriptionData);
                fluxSink.onCancel(() -> subscriptions.remove(uuid));

                ServerSentEvent<Object> successfulSubscribeEvent = ServerSentEvent.builder((Object)("Subscribed: " + userId)).build();
                fluxSink.next(successfulSubscribeEvent);
            })));
    }


    public Mono<ServerResponse> sendNewMessageNotification(ServerRequest request){
        ServerSentEvent<Object> event = ServerSentEvent
            .builder((Object)"new message")
            .build();

        subscriptions.forEach((uuid, subscriptionData) -> subscriptionData.getFluxSink().next(event));

        return ServerResponse.ok().build();
    }
}
