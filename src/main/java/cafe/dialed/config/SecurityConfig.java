package cafe.dialed.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("#{'${app.cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;

    /* LOCAL SECURITY FOR TESTING PURPOSES ONLY */
    @Bean
    @Profile("local")
    public SecurityFilterChain localFilterChain(HttpSecurity http) throws Exception {
        http
            // Enable Cross-origin resource sharing
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Cross Site Request Forgery disabled for webhooks? Must be a better way...
            .csrf(AbstractHttpConfigurer::disable)
            // All endpoints require authentication
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/healthcheck").permitAll() // Allows Coolify (or, well, anyone) to confirm the API is working
                    .requestMatchers(toH2Console()).permitAll()
                    .requestMatchers("/webhooks/**").permitAll() // Webhooks can be hit by anyone
                    .anyRequest().authenticated() // All other requests must be authenticated!
            )
            // Configure OAuth2 Resource Server to process JWTs
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            // Set session management to stateless, as we are using JWTs
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    /* PRODUCTION SECURITY RULES */
    @Bean
    @Profile("production")
    public SecurityFilterChain productionFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable Cross-origin resource sharing
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Cross Site Request Forgery disabled for webhooks? Must be a better way...
                .csrf(AbstractHttpConfigurer::disable)
                // All endpoints require authentication
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/healthcheck").permitAll() // Allows Coolify (or, well, anyone) to confirm the API is working
                        .requestMatchers("/webhooks/**").permitAll() // webhooks can be hit by anyone
                        .anyRequest().authenticated() // all other requests must be authenticated!
                )
                // Configure OAuth2 Resource Server to process JWTs
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                // Set session management to stateless, as we are using JWTs
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    // Since the front-end is not on the same origin/domain, configure CORS
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}