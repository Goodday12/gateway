package com.master.gateway;

import com.master.gateway.filters.PrincipalGitHubTokenRelayGatewayFilterFactory;
import com.master.gateway.utils.JwtUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableWebFluxSecurity
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf().disable()
                .cors().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .authorizeExchange()
                .anyExchange()
                .authenticated().and()
                .oauth2Login();
        return http.build();
    }


    @Bean
    public PrincipalGitHubTokenRelayGatewayFilterFactory principalGitHubTokenRelayGatewayFilterFactory(
            JwtUtils jwtUtils) {
        return new PrincipalGitHubTokenRelayGatewayFilterFactory(jwtUtils);
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder, PrincipalGitHubTokenRelayGatewayFilterFactory filterFactory) {
        return builder.routes()
                .route("user", r -> r.path("/user/**", "/users/**")
                        .filters(f -> f.filter(filterFactory.apply()))
                        .uri("lb://user-service"))
                .build();
    }

}
