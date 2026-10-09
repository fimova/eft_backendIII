
package com.duoc.bff_atm.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

//reenvía el jwt del atm cuando el bff llama al servicio, para que el MS pueda validar el token
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Authentication authentication = SecurityContextHolder.getContext()
                    .getAuthentication();

            if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
                String token = jwtAuthentication.getToken()
                        .getTokenValue();

                requestTemplate.header(
                        "Authorization",
                        "Bearer " + token);
            }
        };
    }
}
