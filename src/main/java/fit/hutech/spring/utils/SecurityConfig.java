package fit.hutech.spring.utils;

import fit.hutech.spring.services.OAuthService;
import fit.hutech.spring.services.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

        private final OAuthService oAuthService;
        private final UserService userService;

        @Bean
        public UserDetailsService userDetailsService() {
                return userService; // Use injected userService instead of 'new' to preserve wiring
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                var auth = new DaoAuthenticationProvider();
                auth.setUserDetailsService(userDetailsService());
                auth.setPasswordEncoder(passwordEncoder());
                return auth;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {
                return http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/css/**", "/js/**", "/images/**", "/", "/oauth/**",
                                                                "/login", "/register", "/error")
                                                .permitAll()
                                                .requestMatchers("/books/edit/**", "/books/add", "/books/delete")
                                                .authenticated()
                                                .requestMatchers("/books/**", "/cart/**", "/profile/**",
                                                                "/orders/**")
                                                .authenticated()
                                                .requestMatchers("/api/**")
                                                .authenticated()
                                                .requestMatchers("/admin/**")
                                                .hasAuthority("ADMIN")
                                                .anyRequest().authenticated())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login")
                                                .deleteCookies("JSESSIONID")
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .permitAll())
                                .formLogin(formLogin -> formLogin
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .defaultSuccessUrl("/", true)
                                                .failureUrl("/login?error")
                                                .permitAll())
                                .oauth2Login(
                                                oauth2Login -> oauth2Login
                                                                .loginPage("/login")
                                                                .failureUrl("/login?error")
                                                                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                                                                .userService(oAuthService))
                                                                .successHandler(
                                                                                (request, response, authentication) -> {
                                                                                        var oidcUser = (DefaultOidcUser) authentication
                                                                                                        .getPrincipal();
                                                                                        userService.saveOauthUser(
                                                                                                        oidcUser.getEmail(),
                                                                                                        oidcUser.getName());
                                                                                        response.sendRedirect("/");
                                                                                })
                                                                .permitAll())
                                .rememberMe(rememberMe -> rememberMe
                                                .key("hutech")
                                                .rememberMeCookieName("hutech")
                                                .tokenValiditySeconds(24 * 60 * 60)
                                                .userDetailsService(userDetailsService()))
                                .exceptionHandling(exceptionHandling -> exceptionHandling.accessDeniedPage("/403"))
                                .sessionManagement(sessionManagement -> sessionManagement.maximumSessions(1)
                                                .expiredUrl("/login"))
                                .httpBasic(httpBasic -> httpBasic
                                                .realmName("hutech"))
                                .build();
        }
}
