package org.rhydo.microgateway.configs;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // micro-product router
                .route("micro-product", r -> r
                        .path("/api/products/**")
                        .filters(f -> f.circuitBreaker(config -> config
                                .setName("ecomBreaker")
                                .setFallbackUri("forward:/fallback/products")))
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
