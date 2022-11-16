package antifraud.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
//@Configuration
@EnableWebSecurity
@EnableMethodSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

    @Bean
    @Lazy
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public UserDetailsService userDetailsService(UserRepository userRepository) {
//        return username -> userRepository.findByUsernameIgnoreCase(username)
//                                  .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
//    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring()
//                           .mvcMatchers(HttpMethod.POST, "api/auth/user");
//    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.httpBasic(c -> c.authenticationEntryPoint((request, response, authException) -> {
                        log.info("HttpBasic: req:{} ,mes:{}", request.getRequestURL(), authException.getMessage());
                        response.sendError(HttpStatus.UNAUTHORIZED.value(), authException.getMessage());
                    }))
                    .csrf().disable()
                    .headers().frameOptions().disable()
                    .and()
                    .authorizeRequests(c -> c
                            .mvcMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                            .mvcMatchers("/actuator/shutdown").permitAll()
                            .anyRequest().authenticated()
                    )
//                    .exceptionHandling(c -> c.authenticationEntryPoint((request, response, authException) -> {
//                        log.info("Exception: req:{} ,mes:{}", request.getRequestURL(), authException.getMessage());
//                        response.sendError(HttpStatus.UNAUTHORIZED.value(), authException.getMessage());
//                    }))
                    .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return httpSecurity.build();
    }
}
