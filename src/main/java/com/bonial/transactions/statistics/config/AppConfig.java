package com.bonial.transactions.statistics.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.RequestContextFilter;

public class AppConfig {

    @Bean
    public FilterRegistrationBean deRegisterRequestContextFilterBean(RequestContextFilter requestContextFilter) {
        FilterRegistrationBean bean = new FilterRegistrationBean(requestContextFilter);
        bean.setEnabled(false);
        return bean;
    }
}
