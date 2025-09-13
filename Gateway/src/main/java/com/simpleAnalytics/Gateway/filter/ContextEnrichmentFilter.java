package com.simpleAnalytics.Gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpleAnalytics.Gateway.entity.Context;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ua_parser.Client;
import ua_parser.Parser;

import java.util.HashMap;
import java.util.Map;

@Component
public class ContextEnrichmentFilter implements GlobalFilter, Ordered {

    private final Parser uaParser = new Parser();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 1. Extract IP
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip == null && request.getRemoteAddress() != null) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }
        String locale = request.getHeaders().getFirst("Accept-Language");

        // 2. Extract User-Agent
        String ua = request.getHeaders().getFirst("User-Agent");
        Client client = ua != null ? uaParser.parse(ua) : null;

        String os = client != null ? client.os.family : "unknown";
        String device = client != null ? client.device.family : "unknown";
        String browser = client != null ? client.userAgent.family : "unknown";
        // 3. Enrich context and add to exchange attributes
        Context ctx = Context.builder()
                .ip(ip).browser(browser).device(device).os(os).locale(locale).build();

        exchange.getAttributes().put("contextData", ctx);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // Run early
    }
}
