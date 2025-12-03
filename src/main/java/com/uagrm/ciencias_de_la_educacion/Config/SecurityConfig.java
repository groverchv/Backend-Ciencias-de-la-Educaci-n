package com.uagrm.ciencias_de_la_educacion.Config;

import com.uagrm.ciencias_de_la_educacion.Security.JwtAuthenticationEntryPoint;
import com.uagrm.ciencias_de_la_educacion.Security.JwtAuthenticationFilter;
import com.uagrm.ciencias_de_la_educacion.Security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CorsConfig corsConfig;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Configurar origins
        String origins = corsConfig.getAllowed().getOrigins();
        if ("*".equals(origins)) {
            configuration.addAllowedOriginPattern("*");
        } else {
            configuration.setAllowedOrigins(Arrays.asList(origins.split(",")));
        }

        // Configurar métodos
        String methods = corsConfig.getAllowed().getMethods();
        configuration.setAllowedMethods(Arrays.asList(methods.split(",")));

        // Configurar headers
        String headers = corsConfig.getAllowed().getHeaders();
        if ("*".equals(headers)) {
            configuration.addAllowedHeader("*");
        } else {
            configuration.setAllowedHeaders(Arrays.asList(headers.split(",")));
        }

        // Headers expuestos
        String exposedHeaders = corsConfig.getExposed().getHeaders();
        if (exposedHeaders != null && !exposedHeaders.isEmpty()) {
            configuration.setExposedHeaders(Arrays.asList(exposedHeaders.split(",")));
        }

        // Credentials
        configuration.setAllowCredentials(corsConfig.getAllow().getCredentials());

        // Max Age
        configuration.setMaxAge(corsConfig.getMax().getAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/visitas/**").permitAll() // Allow visit tracking for all users
                        // WebSocket endpoints
                        .requestMatchers("/ws/**").permitAll()
                        // Menus - Allow GET for public navigation
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/menu/**").permitAll()
                        .requestMatchers("/api/menu/**").authenticated()
                        // SubMenus - Allow GET for public navigation
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/sub_menu/**").permitAll()
                        .requestMatchers("/api/sub_menu/**").authenticated()
                        // Presentacion - Allow GET for slider
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/presentacion/**").permitAll()
                        .requestMatchers("/api/presentacion/**").authenticated()
                        // Contenidos
                        .requestMatchers("/api/contenidos/ruta").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/contenidos/**").permitAll()
                        .requestMatchers("/api/contenidos/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/contenido/**").permitAll() // Singular
                        .requestMatchers("/api/contenido/**").authenticated()
                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
