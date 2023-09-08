package org.top.librarymvcapp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.top.librarymvcapp.rdb.repository.UserRepository;

import org.top.librarymvcapp.rdb.security.RdbUserDetailsService;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// Класс конфигурации Spring Security
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    // Конфигурация, управляющая доступом к обработчикам (запросам)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // конфигурирвоание
        http
                .authorizeHttpRequests((request) -> request
                        .requestMatchers("/", "webjars/**", "images/**", "css/**").permitAll()
                        .requestMatchers("/genre/**").hasRole("ADMIN")
                        .requestMatchers("/author/**").hasRole("ADMIN")
                        .requestMatchers("/publication/**").hasRole("ADMIN")
                        .requestMatchers("/book/new").hasRole("ADMIN")
                        .requestMatchers("develop/create-admin", "develop/create-user").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .permitAll()
                        .failureUrl("/login?error=true")
                        .successHandler(successHandler())
//                        .successForwardUrl("/")
                ).logout((customizer) -> customizer
                        .logoutSuccessUrl("/login")
                );
        return http.build();
    }

    // стандартный Spring password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // зависимость репозитория
    public final UserRepository userRepository;

    // Конфигурация UserDetailsService - провайдер работы с пользователями
    // версия для работы с rdb
    @Bean
    public UserDetailsService userDetailsService() {
        return new RdbUserDetailsService(userRepository);
    }

    // Зависимости, необходимые для работы Spring Security
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    private final DataSource dataSource;

    @Bean
    public UserDetailsManager users(HttpSecurity http) throws Exception {

        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder())
                .and()
                .authenticationProvider(daoAuthenticationProvider())
                .build();
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setAuthenticationManager(authenticationManager);
        return jdbcUserDetailsManager;
    }
    //    ******************************************************
    @Bean
    public AuthenticationSuccessHandler successHandler(){
        return new MySimpleUrlAuthenticationSuccessHandler();
    }
    //*************************************************************
    public static class MySimpleUrlAuthenticationSuccessHandler
            implements AuthenticationSuccessHandler {

        protected Log logger = LogFactory.getLog(this.getClass());

        private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request,
                                            HttpServletResponse response, Authentication authentication)
                throws IOException {

            handle(request, response, authentication);
            clearAuthenticationAttributes(request);
        }

        protected void handle(
                HttpServletRequest request,
                HttpServletResponse response,
                Authentication authentication
        ) throws IOException {

            String targetUrl = determineTargetUrl(authentication);
            if (response.isCommitted()) {
                logger.debug(
                        "Response has already been committed. Unable to redirect to "
                                + targetUrl);
                return;
            }

            redirectStrategy.sendRedirect(request, response, targetUrl);
        }
        protected String determineTargetUrl(final Authentication authentication) {

            Map<String, String> roleTargetUrlMap = new HashMap<>();
            roleTargetUrlMap.put("ROLE_USER", "/user");
            roleTargetUrlMap.put("ROLE_ADMIN", "/");

            final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (final GrantedAuthority grantedAuthority : authorities) {
                String authorityName = grantedAuthority.getAuthority();
                if(roleTargetUrlMap.containsKey(authorityName)) {
                    return roleTargetUrlMap.get(authorityName);
                }
            }

            throw new IllegalStateException();
        }

        protected void clearAuthenticationAttributes(HttpServletRequest request) {
            HttpSession session = request.getSession(false);
            if (session == null) {
                return;
            }
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}

