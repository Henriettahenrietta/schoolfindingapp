package com.schoolfinder.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(private val firebaseTokenFilter: FirebaseTokenFilter) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsSource()) }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    // Public discovery surface (guests)
                    .requestMatchers(HttpMethod.GET, "/api/v1/schools/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/meta/**").permitAll()
                    .requestMatchers("/actuator/health", "/error").permitAll()
                    // H2 console — only registered under the `local` profile (dev only)
                    .requestMatchers("/h2-console/**").permitAll()
                    // Admin surface
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                    // Everything else needs an authenticated student
                    .anyRequest().authenticated()
            }
            .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            // Allow the H2 console (local profile) to render in a frame
            .headers { headers -> headers.frameOptions { it.sameOrigin() } }

        return http.build()
    }

    private fun corsSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOriginPatterns = listOf("*")
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
        }
        return UrlBasedCorsConfigurationSource().apply { registerCorsConfiguration("/**", config) }
    }
}
