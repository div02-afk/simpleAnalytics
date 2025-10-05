package com.simpleAnalytics.Gateway.config;

import com.simpleAnalytics.Gateway.filter.ContextEnrichmentFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ContextEnrichmentFilter> myFilterRegistration(ContextEnrichmentFilter filter) {
        FilterRegistrationBean<ContextEnrichmentFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
//        registrationBean.addUrlPatterns("*"); // only REST controller paths
        registrationBean.setOrder(1); // filter order
        return registrationBean;
    }
}
