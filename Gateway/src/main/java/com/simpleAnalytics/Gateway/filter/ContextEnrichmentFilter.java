package com.simpleAnalytics.Gateway.filter;

import com.simpleAnalytics.Gateway.entity.Context;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Order(1)
public class ContextEnrichmentFilter implements Filter {

    private final UserAgentAnalyzer uaParser = UserAgentAnalyzer
            .newBuilder()
            .hideMatcherLoadStats()
            .withCache(10000)
            .build();;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 1. Extract IP
        String ip = getClientIp(httpRequest);
        String locale = String.valueOf(httpRequest.getHeader("accept-language"));

        // 2. Extract User-Agent
        String ua = httpRequest.getHeader("user-agent");
        UserAgent client = ua != null ? uaParser.parse(ua) : null;

        String os = client != null ? client.getValue("OperatingSystemNameVersion") : "unknown";
        String device = client != null ? client.getValue("DeviceName") : "unknown";
        String browser = client != null ? client.getValue("AgentNameVersion") : "unknown";
        // 3. Enrich context and add to exchange attributes
        Context ctx = Context.builder()
                .userAgent(ua)
                .timezone("")
                .ip(ip).browser(browser).device(device).os(os).locale(locale).build();
//        log.info("Added context: {}", ctx.toString());
        request.setAttribute("context", ctx);
        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
