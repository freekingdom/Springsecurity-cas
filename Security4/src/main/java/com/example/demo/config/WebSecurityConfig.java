package com.example.demo.config;

import java.util.Arrays;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import com.example.demo.service.CustomUserService;
import com.example.demo.service.MyFilterSecurityInterceptor;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
    private MyFilterSecurityInterceptor myFilterSecurityInterceptor;

	
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private SingleSignOutFilter singleSignOutFilter;

    @Autowired
    private LogoutFilter logoutFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    
    /*
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserService).passwordEncoder(NoOpPasswordEncoder.getInstance()); //user Details Service验证

    }*/
    
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        //设置cas认证提供
        return new ProviderManager(Arrays.asList(authenticationProvider));
    }



    @Bean
    public CasAuthenticationFilter casAuthenticationFilter(ServiceProperties sp)
            throws Exception {
        //cas认证过滤器，当触发本filter时，对ticket进行认证
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        //filter.setServiceProperties(sp);
        filter.setAuthenticationManager(authenticationManager());
        filter.setFilterProcessesUrl("/login/cas");
        filter.setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/"));
        filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/error"));
        return filter;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
    	
    	http               
        .authorizeRequests()
        .and()
        .authorizeRequests().antMatchers("login/cas").permitAll()
        .and()
        .httpBasic().authenticationEntryPoint(authenticationEntryPoint)
        .and()
        .logout().logoutSuccessUrl("/logout")
        .and()
        .addFilterBefore(singleSignOutFilter, CasAuthenticationFilter.class).addFilterBefore(logoutFilter, LogoutFilter.class);
    	http.addFilterBefore(myFilterSecurityInterceptor, FilterSecurityInterceptor.class);
        /*http.authorizeRequests()
                .anyRequest().authenticated() //任何请求,登录后可以访问
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/admin")
                .failureUrl("/login?error")
                .permitAll() //登录页面用户任意访问
                .and()
                .logout().permitAll(); //注销行为任意访问
        http.addFilterBefore(myFilterSecurityInterceptor, FilterSecurityInterceptor.class);*/
    }
    
    @Override
    public void configure(WebSecurity web) throws Exception {
        //解决静态资源被拦截的问题
        web.ignoring().antMatchers("/static/**");
    }

}
