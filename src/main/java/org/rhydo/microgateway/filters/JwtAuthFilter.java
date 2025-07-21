package org.rhydo.microgateway.filters;

import org.apache.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;

@Component
public class JwtAuthFilter implements WebFilter {

    @Override
    public @NonNull Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String authHeader = exchange.getRequest()
                .getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setRawStatusCode(HttpStatus.UNAUTHORIZED.value());
            return exchange.getResponse().setComplete();
        }

        // TODO: Validate JWT Token

        return chain.filter(exchange);
    }
}
