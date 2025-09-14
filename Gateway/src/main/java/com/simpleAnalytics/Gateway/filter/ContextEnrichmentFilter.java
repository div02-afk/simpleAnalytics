package com.simpleAnalytics.Gateway.filter;

import com.simpleAnalytics.Gateway.entity.Context;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua_parser.Client;
import ua_parser.Parser;
import java.io.IOException;

@Slf4j
@Component
@Order(1)
public class ContextEnrichmentFilter extends OncePerRequestFilter {

    private final Parser uaParser = new Parser();

    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws ServletException, IOException {
        // 1. Extract IP
        String ip = String.valueOf(request.getHeaders("X-Forwarded-For"));

//        if (ip == null && request.getRemoteAddress() != null) {
//            ip = request.getRemoteAddress().getAddress().getHostAddress();
//        }
        String locale = String.valueOf(request.getHeaders("Accept-Language"));

        // 2. Extract User-Agent
        String ua = String.valueOf(request.getHeaders("User-Agent"));
        Client client = ua != null ? uaParser.parse(ua) : null;

        String os = client != null ? client.os.family : "unknown";
        String device = client != null ? client.device.family : "unknown";
        String browser = client != null ? client.userAgent.family : "unknown";
        // 3. Enrich context and add to exchange attributes
        Context ctx = Context.builder()
                .ip(ip).browser(browser).device(device).os(os).locale(locale).build();
        log.info("Added context: {}", ctx.toString());
        request.setAttribute("context", ctx);

        filterChain.doFilter(request, response);
    }
}
