package com.PPOOII.Laboratorio.Config;

import com.PPOOII.Laboratorio.Config.Model.Constans;
import com.PPOOII.Laboratorio.Entities.Usuario;
import com.PPOOII.Laboratorio.Repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Component
public class ApiKeyAuthorizationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "APIKey";

    private final UsuarioRepository usuarioRepository;

    public ApiKeyAuthorizationFilter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String requestMethod = Objects.requireNonNull(request.getMethod(), "request method no puede ser null");
        String uri = request.getRequestURI();

        // Bypass total para Swagger, Login, Options
        if (HttpMethod.OPTIONS.matches(requestMethod)
            || uri.endsWith(Constans.LOGIN_URL)
            || uri.startsWith("/swagger-ui/")
            || uri.startsWith("/v3/api-docs/")
            || uri.equals("/swagger-ui.html")) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean isPublicEndpoint = uri.startsWith("/public/") 
            || uri.startsWith("/LaboratorioV1/public/")
            || uri.startsWith("/api/demo/");

        String apiKey = request.getHeader(API_KEY_HEADER);

        // Para endpoints publicos, validar la APIKey contra la BD sin pedir JWT
        if (isPublicEndpoint) {
            if (apiKey == null || apiKey.isBlank()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("APIKey requerida para consulta publica");
                return;
            }
            boolean keyValid = usuarioRepository.findAll().stream()
                .anyMatch(u -> apiKey.equals(u.getApikey()));
                
            if (!keyValid) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("APIKey invalida");
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

        // Para endpoints privados, validar autenticacion JWT y la APIKey personal del usuario
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (apiKey == null || apiKey.isBlank()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("APIKey requerida");
            return;
        }

        Optional<Usuario> usuario = usuarioRepository.findByUsername(authentication.getName());
        if (usuario.isEmpty() || usuario.get().getApikey() == null || !usuario.get().getApikey().equals(apiKey)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("APIKey invalida");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
