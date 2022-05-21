package com.master.gateway;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExp;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExpFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.util.PatternMatchUtils;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder, TokenRelayGatewayFilterFactory filterFactory) {
        return builder.routes()
                .route("user", r -> r.path("/user/**", "/users/**")
                        .filters(f -> f.filter(filterFactory.apply()))
                        .uri("lb://user-service"))
                .build();
    }

}
