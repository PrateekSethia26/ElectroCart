package com.electrocart.api_gateway.security;

import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.http.HttpHeaders;

@Component
public class JwtAuthenticationFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService){
        this.jwtService = jwtService;
    }

    public HandlerFilterFunction<ServerResponse, ServerResponse> apply(){
        return (request, next) -> {
            String authorization =
                    request.headers()
                            .firstHeader(HttpHeaders.AUTHORIZATION);

            if(authorization == null || !authorization.startsWith("Bearer ")){
                return ServerResponse
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("""
                                {
                                    "status": 401,
                                    "message": "Authentication required"
                                }
                              """);
            }

            String token = authorization.substring(7);

            try{

                Claims claims = jwtService.validateToken(token);
                return next.handle(request);

            } catch (Exception e) {
                return ServerResponse
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("""
                                {
                                    "status": 401,
                                    "message": "Invalid or expired token"
                                }
                             """);
            }
        };
    }
}
