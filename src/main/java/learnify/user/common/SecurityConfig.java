package learnify.user.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import learnify.user.service.MyUserDetailsService;
import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final MyUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final MyAuthEntryPoint myAuthEntryPoint;
    private final MyAccessDeniedHandler myAccessDeniedHandler;

    @Bean
    SecurityFilterChain security(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .httpBasic(h -> h.disable())
            .formLogin(f -> f.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authenticationProvider(authprovider())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").authenticated()
                .anyRequest().permitAll())
                
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(myAuthEntryPoint)
                .accessDeniedHandler(myAccessDeniedHandler)
            );
        return http.build();
    }

    @Bean
    AuthenticationManager authManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authprovider() {
        DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider(userDetailsService);
        daoAuthProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthProvider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }
}
