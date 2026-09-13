package com.electrocart.api_gateway.config;

import com.electrocart.api_gateway.security.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class GatewayRoutesConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayRoutesConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RouterFunction<ServerResponse> userRoutes() {

        return route("user-auth")
                .route(
                        path("/api/auth/**"),
                        HandlerFunctions.http()
                )
                .filter(lb("USER-SERVICE"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> protectedUserRoutes() {

        return route("user-service")
                .route(
                        path("/api/users/**"),
                        HandlerFunctions.http()
                )
                .filter(lb("USER-SERVICE"))
                .filter(jwtAuthenticationFilter.apply())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productRoutes(){
        return route("product-service")
                .route(
                        path("/api/products/**"),
                        HandlerFunctions.http()
                )
                .filter(lb("PRODUCT-SERVICE"))
                .build();
    }
}
