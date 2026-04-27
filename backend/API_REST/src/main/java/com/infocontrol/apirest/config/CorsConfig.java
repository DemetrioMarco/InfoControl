package com.infocontrol.apirest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${app.cors.origins}")
    private List<String> allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 1. Permitir credenciales (Cookies, Auth Headers)
        config.setAllowCredentials(true);

        // 2. Orígenes permitidos (Extraer a variable de entorno en producción)
        config.setAllowedOrigins(allowedOrigins);

        // 3. Headers permitidos
        config.setAllowedHeaders(List.of(
                "Origin", "Content-Type", "Accept", "Authorization",
                "X-Requested-With", "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // 4. MÉTODOS PERMITIDOS (Añadido PATCH que faltaba)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // 5. Tiempo de vida de la respuesta Preflight (Cache)
        // Evita que el navegador pregunte por CORS en cada petición por 1 hora
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
