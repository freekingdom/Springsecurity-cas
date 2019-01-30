package com.example.demo.config;

import javax.servlet.http.HttpSessionEvent;

import org.apache.catalina.webresources.Cache;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorageImpl;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.authentication.EhCacheBasedTicketCache;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.example.demo.service.CustomUserService;


@Configuration
public class CasSecurityConfig {
	
	@Autowired
	private CustomUserService customUserService;
	//cas服务
    @Value("${cas.server.url:http://localhost:8080/cas}")
    private String casServerUrl;
    
    @Autowired
    public static ProxyGrantingTicketStorageImpl pgtStorage;

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        //本机服务，访问login/cas时进行校验登录
        serviceProperties.setService("http://localhost:9999/demo/login/cas");
        return serviceProperties;
    }

    @Bean
    @Primary
    public AuthenticationEntryPoint authenticationEntryPoint(
            ServiceProperties sP) {

        CasAuthenticationEntryPoint entryPoint
                = new CasAuthenticationEntryPoint();
        //cas登录服务
        entryPoint.setLoginUrl(casServerUrl + "/login");
        entryPoint.setServiceProperties(sP);
        return entryPoint;
    }

    @Bean
    public TicketValidator ticketValidator() {
        //指定cas校验器
        return new Cas20ServiceTicketValidator(
                casServerUrl);
    }

    //cas认证
    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {

        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setServiceProperties(serviceProperties());
        provider.setTicketValidator(ticketValidator());
        //固定响应用户，在生产环境中需要额外设置用户映射
        provider.setKey("securitytest");
        provider.setAuthenticationUserDetailsService(customUserService);
        return provider;

    }


    @Bean
    public SecurityContextLogoutHandler securityContextLogoutHandler() {
        return new SecurityContextLogoutHandler();
    }

    @Bean
    public LogoutFilter logoutFilter() {
        //退出后转发路径
        LogoutFilter logoutFilter = new LogoutFilter(
                casServerUrl + "/logout",
                securityContextLogoutHandler());
        //cas退出
        logoutFilter.setFilterProcessesUrl("/cas/logout");
        return logoutFilter;
    }

    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        //单点退出
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix(casServerUrl);
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        return singleSignOutFilter;
    }

    //设置退出监听
    @EventListener
    public SingleSignOutHttpSessionListener singleSignOutHttpSessionListener(
            HttpSessionEvent event) {
        return new SingleSignOutHttpSessionListener();
    }

}
