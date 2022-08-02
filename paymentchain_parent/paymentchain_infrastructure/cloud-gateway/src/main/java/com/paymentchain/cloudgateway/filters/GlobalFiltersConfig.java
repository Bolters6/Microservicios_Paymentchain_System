package com.paymentchain.cloudgateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GlobalFiltersConfig {

    Logger log = LoggerFactory.getLogger(GlobalFiltersConfig.class);

    @Bean
    public GlobalFilter preGlobalFilter(){
        return (exchange, chain) -> {
            log.info("Pre Filter");
           return chain.filter(exchange);
        };
    }
    @Bean
    public GlobalFilter postGlobalFilter(){
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> log.info("Post Filter")));
    }
}
