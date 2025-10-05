package com.simpleAnalytics.Gateway.filter;

import com.simpleAnalytics.Gateway.entity.Context;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
import ua_parser.Client;
import ua_parser.Parser;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(1)
public class ContextEnrichmentFilter implements Filter {

    private final Parser uaParser = new Parser();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        log.info("Incoming request: {} {}", httpRequest.getMethod(), httpRequest.getRequestURL());
        log.info("Headers: {}", Collections.list(httpRequest.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(h -> h, httpRequest::getHeader)));

        // 1. Extract IP
        String ip = getClientIp(httpRequest);
        String locale = String.valueOf(httpRequest.getHeader("accept-language"));

        // 2. Extract User-Agent
        String ua = httpRequest.getHeader("user-agent");
        Client client = ua != null ? uaParser.parse(ua) : null;

        String os = client != null ? client.os.family : "unknown";
        String device = client != null ? client.device.family : "unknown";
        String browser = client != null ? client.userAgent.family : "unknown";
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
