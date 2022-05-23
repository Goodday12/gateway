package com.master.gateway.filters;

import com.master.gateway.utils.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.server.ServerWebExchange;

public class PrincipalGitHubTokenRelayGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final JwtUtils jwtUtils;

    public PrincipalGitHubTokenRelayGatewayFilterFactory(JwtUtils jwtUtils) {
        super(Object.class);
        this.jwtUtils = jwtUtils;
    }

    public GatewayFilter apply() {
        return apply((Object) null);
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> exchange.getPrincipal()
                .filter(principal -> principal instanceof OAuth2AuthenticationToken)
                .cast(OAuth2AuthenticationToken.class)
                .map(oAuth2AuthenticationToken -> withBearerAuth(exchange, oAuth2AuthenticationToken))
                .defaultIfEmpty(exchange).flatMap(chain::filter);
    }

    private ServerWebExchange withBearerAuth(ServerWebExchange exchange, OAuth2AuthenticationToken accessToken) {
        return exchange.mutate().request(r -> r.headers(headers -> headers.setBearerAuth(jwtUtils.generateToken(accessToken))))
                .build();
    }

}