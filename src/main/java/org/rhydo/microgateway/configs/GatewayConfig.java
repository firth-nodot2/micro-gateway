package org.rhydo.microgateway.configs;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
public class GatewayConfig {

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1,1,1);
    }

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(
                Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getHostName());
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           RedisRateLimiter redisRateLimiter,
                                           KeyResolver userKeyResolver) {
        return builder.routes()
                .route("micro-product", r -> r
                        .path("/api/products/**")
                        .filters(f -> f
                                .retry(config -> config
                                        .setRetries(3)
                                        .setMethods(HttpMethod.GET))
                                .circuitBreaker(config -> config
                                        .setName("ecomBreaker")
                                        .setFallbackUri("forward:/fallback/products"))
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter)
                                        .setKeyResolver(userKeyResolver))
                        )
                        .uri("lb://MICRO-PRODUCT"))


                // micro-user router
                .route("micro-user", r -> r
                        .path("/api/users/**")
                        .uri("lb://MICRO-USER"))

                // micro-order router，match two paths
                .route("micro-order", r -> r
                        .path("/api/orders/**", "/api/cart/**")
                        .uri("lb://MICRO-ORDER"))
                .build();
    }
}
