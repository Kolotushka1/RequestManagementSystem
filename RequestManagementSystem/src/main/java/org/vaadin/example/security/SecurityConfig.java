package org.vaadin.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.vaadin.example.domain.AppUser;
import org.vaadin.example.repository.UserRepository;
import org.vaadin.example.service.UserService;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/VAADIN/**",
                                "/frontend/**",
                                "/icons/**",
                                "/line-awesome/**",
                                "/login",
                                "/register",
                                "/public/**",
                                "/"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .cors(cors ->
                        cors
                                .configurationSource(corsConfig())
                )
                .csrf(csrf -> csrf
                        .disable()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration,
                                                       DaoAuthenticationProvider authenticationProvider) throws Exception {
        return new ProviderManager(authenticationProvider);
    }

    CorsConfigurationSource corsConfig() {
        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("https://yourdomain.com", "https://anotherdomain.com")); // # for change
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Location"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_OPERATOR > ROLE_CLIENT");
        return hierarchy;
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            AppUser appUser = userRepository.findByName(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
            return new org.springframework.security.core.userdetails.User(
                    appUser.getName(),
                    appUser.getPasswordHash(),
                    List.of(new SimpleGrantedAuthority(appUser.getRole().toString()))
            );
        };
    }

}
