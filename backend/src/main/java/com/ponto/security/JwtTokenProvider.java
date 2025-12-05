package com.ponto.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    // Usar Keys.hmacShaKeyFor para garantir chave segura
    public Key getSigningKey() {
        try {
            System.out.println("=== GERANDO CHAVE DE ASSINATURA ===");
            System.out.println("Secret key string length: " + jwtSecret.length());
            
            // Se a chave for uma string base64, decode-a
            if (jwtSecret.length() >= 64 && isBase64(jwtSecret)) {
                byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
                System.out.println("Base64 decoded key bytes: " + keyBytes.length);
                return Keys.hmacShaKeyFor(keyBytes);
            } 
            // Se não for base64, use a string direta
            else {
                byte[] keyBytes = jwtSecret.getBytes();
                System.out.println("Raw key bytes: " + keyBytes.length);
                
                // Para HS512 precisamos de pelo menos 64 bytes
                if (keyBytes.length < 64) {
                    // Expande a chave para 64 bytes repetindo se necessário
                    byte[] expandedKey = new byte[64];
                    for (int i = 0; i < 64; i++) {
                        expandedKey[i] = keyBytes[i % keyBytes.length];
                    }
                    return Keys.hmacShaKeyFor(expandedKey);
                }
                return Keys.hmacShaKeyFor(keyBytes);
            }
        } catch (Exception e) {
            System.err.println("ERRO ao criar chave de assinatura: " + e.getMessage());
            throw new RuntimeException("Falha ao criar chave JWT", e);
        }
    }

    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao extrair claims do token", e);
        }
    }

    // Método para extrair role do token
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    // Método para extrair userId do token
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }
    
    private boolean isBase64(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    public String criarToken(String username, String role, Long userId) {
        System.out.println("=== GERANDO TOKEN JWT ===");
        System.out.println("Username: " + username);
        System.out.println("Role: " + role);
        System.out.println("UserId: " + userId);
        
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpiration);
            
            Key signingKey = getSigningKey();
            System.out.println("Signing key algorithm: " + signingKey.getAlgorithm());
            
            String token = Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("role", role)
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
            
            System.out.println("Token gerado com sucesso!");
            System.out.println("Token length: " + token.length());
            return token;
            
        } catch (Exception e) {
            System.err.println("ERRO detalhado ao criar token: " + e.getClass().getName());
            System.err.println("Mensagem: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Falha ao gerar token JWT: " + e.getMessage(), e);
        }
    }
    
    public boolean validarToken(String token) {
        System.out.println("=== VALIDANDO TOKEN ===");
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            System.out.println("Token VÁLIDO");
            debugToken(token); // Para ver o conteúdo
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("Token EXPIRADO: " + e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Token INVÁLIDO: " + e.getMessage());
            System.err.println("Tipo de erro: " + e.getClass().getName());
            return false;
        }
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.getSubject();
    }

    public void debugToken(String token) {
        try {
            System.out.println("=== DEBUG TOKEN ===");
            System.out.println("Token completo: " + token);
            
            Claims claims = getClaimsFromToken(token);
            System.out.println("Subject: " + claims.getSubject());
            System.out.println("Role: " + claims.get("role"));
            System.out.println("UserId: " + claims.get("userId"));
            System.out.println("Issued At: " + claims.getIssuedAt());
            System.out.println("Expiration: " + claims.getExpiration());
            System.out.println("=== FIM DEBUG ===");
        } catch (Exception e) {
            System.err.println("Erro no debug do token: " + e.getMessage());
        }
    }
}