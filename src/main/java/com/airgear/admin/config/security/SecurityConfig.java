package com.airgear.admin.config.security;

import com.airgear.admin.config.security.filters.JWTAuthorizationFilter;
import com.airgear.admin.config.security.properties.AirGearSecurityProperties;
import com.airgear.admin.dto.UserSaveRequest;
import com.airgear.admin.model.Role;
import com.airgear.admin.service.impl.UserServiceImpl;
import com.airgear.admin.utils.Routes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(AirGearSecurityProperties.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AirGearSecurityProperties securityProperties;
    private final UserServiceImpl userService;

    public SecurityConfig(AirGearSecurityProperties securityProperties,
                          UserServiceImpl userService) {
        this.securityProperties = securityProperties;
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        setupDefaultAdmins();
    }

    private void setupDefaultAdmins() {
        List<UserSaveRequest> requests = securityProperties.getAdmins().entrySet().stream()
                .map(entry -> new UserSaveRequest(
                        entry.getValue().getEmail(),
                        new String(entry.getValue().getPassword()),
                        null,
                        entry.getKey()))
                .peek(admin -> log.info("Default admin found: {} <{}>", admin.name(), admin.email()))
                .collect(Collectors.toList());
        userService.mergeAdmins(requests);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .antMatchers(getPermitAllUrls()).permitAll()
                .antMatchers(HttpMethod.GET, Routes.USERS, Routes.USERS + "/find").hasAnyRole(Role.ADMIN.getValue(), Role.MODERATOR.getValue())
                .antMatchers(Routes.USERS + "/me/**").hasRole(Role.ADMIN.getValue())
                .antMatchers(HttpMethod.GET, Routes.USERS + "/count/**").hasRole(Role.ADMIN.getValue())
                .antMatchers(Routes.USERS + "/{id:\\d+}/**").hasRole(Role.ADMIN.getValue())
                .antMatchers(HttpMethod.POST, Routes.USERS).hasRole(Role.ADMIN.getValue())
                .antMatchers(HttpMethod.GET, Routes.GOODS + "/**").hasAnyRole(Role.ADMIN.getValue(), Role.MODERATOR.getValue())
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(Role.ADMIN.getValue())
                .anyRequest().authenticated()
                .and()
                .addFilter(jwtAuthorizationFilter())
                .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private String[] getPermitAllUrls() {
        List<String> permitAllUrls = new ArrayList<>();
        permitAllUrls.add("/v3/api-docs/**");
        permitAllUrls.add("/swagger-ui/**");
        permitAllUrls.add("/swagger-ui.html");
        return permitAllUrls.toArray(new String[0]);
    }

    private JWTAuthorizationFilter jwtAuthorizationFilter() throws Exception {
        return new JWTAuthorizationFilter(authenticationManager(), securityProperties.getJwt());
    }

    private CorsConfigurationSource corsConfigurationSource() {
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}
