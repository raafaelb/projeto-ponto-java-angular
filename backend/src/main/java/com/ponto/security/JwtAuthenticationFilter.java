package com.ponto.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Enumeration;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) 
            throws ServletException, IOException {
        
        String requestPath = request.getServletPath();
        System.out.println("=== JWT FILTER INICIADO ===");
        System.out.println("Path: " + requestPath);
        System.out.println("Method: " + request.getMethod());
        
        // Log de todos os headers para debug
        System.out.println("=== HEADERS ===");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }
        
        try {
            // 1. Extrair token do cabeçalho Authorization
            String token = extractTokenFromRequest(request);
            System.out.println("Token extraído: " + (token != null ? "SIM" : "NÃO"));
            
            if (token != null) {
                System.out.println("Token length: " + token.length());
                System.out.println("Token preview: " + token.substring(0, Math.min(50, token.length())) + "...");
            }
            
            // 2. Validar token
            if (token != null && jwtTokenProvider.validarToken(token)) {
                System.out.println("Token VÁLIDO");
                
                // 3. Extrair informações do token
                String username = jwtTokenProvider.getUsernameFromToken(token);
                String role = extractRoleFromToken(token);
                
                System.out.println("Usuário autenticado: " + username);
                System.out.println("Role: " + role);
                
                // 4. Criar objeto de autenticação
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + role)
                );
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        username, 
                        null, 
                        authorities
                    );
                
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // 5. Configurar autenticação no contexto de segurança
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                System.out.println("SecurityContext configurado com autenticação");
            } else {
                System.out.println("Token INVÁLIDO ou NÃO ENCONTRADO");
                if (token != null) {
                    System.out.println("Razão: Validação falhou");
                }
            }
        } catch (Exception ex) {
            System.err.println("ERRO no filtro JWT: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        // Verificar o estado atual do SecurityContext
        System.out.println("=== SECURITY CONTEXT ===");
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            System.out.println("Autenticação presente: " + 
                SecurityContextHolder.getContext().getAuthentication().getName());
            System.out.println("Autoridades: " + 
                SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        } else {
            System.out.println("NENHUMA autenticação no SecurityContext");
        }
        
        System.out.println("=== JWT FILTER FINALIZADO ===\n");
        
        // 6. Continuar a cadeia de filtros
        filterChain.doFilter(request, response);
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        // Extrair token do cabeçalho Authorization: Bearer <token>
        String bearerToken = request.getHeader("Authorization");
        
        System.out.println("Header Authorization: " + bearerToken);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            System.out.println("Token extraído do header Bearer");
            return token;
        }
        
        // Também verificar parâmetro de query (opcional)
        String token = request.getParameter("token");
        if (StringUtils.hasText(token)) {
            System.out.println("Token extraído do query parameter");
            return token;
        }
        
        System.out.println("Nenhum token encontrado na requisição");
        return null;
    }
    
    private String extractRoleFromToken(String token) {
        try {
            io.jsonwebtoken.Claims claims = jwtTokenProvider.getClaimsFromToken(token);
            String role = claims.get("role", String.class);
            System.out.println("Role extraída do token: " + role);
            return role;
        } catch (Exception e) {
            System.err.println("ERRO ao extrair role do token: " + e.getMessage());
            return "USER";
        }
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        boolean shouldNotFilter = path.startsWith("/api/auth/") || 
               path.startsWith("/actuator/") || 
               path.equals("/test") ||
               path.equals("/error");
        
        System.out.println("Should not filter? " + shouldNotFilter + " for path: " + path);
        return shouldNotFilter;
    }
}