package com.zerobase.schoolmealboard.component.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  // 비밀키
  @Value("${spring.jwt.secret}")
  private String secretKey;

  // 만료 시간
  private static final long TOKEN_EXPIRED_TIME = 1000 * 60 * 60; // 1시간

  // 알고리즘
  private static final SignatureAlgorithm algorithm = SignatureAlgorithm.HS256;

  private final UserDetailsService userDetailsService;


  // 토큰 생성
  public String generateToken(String email) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRED_TIME))
        .signWith(algorithm, secretKey)
        .compact(); // 토큰을 문자열로 변환하여 반환
  }

  // 토큰에서 정보 추출
  public Claims extractClaims(String token) {
    return Jwts.parser()
        .setSigningKey(secretKey)
        .parseClaimsJws(token)
        .getBody();
  }

  // 토큰에서 이메일 추출
  public String extractUsername(String token) {
    return extractClaims(token).getSubject();
  }

  // 토큰 만료여부 확인
  public boolean isTokenExpired(String token) {
    try {
      return extractClaims(token).getExpiration().before(new Date()); // 현재보다 이전이면 만료
    } catch (ExpiredJwtException e) {
      return true;  // 현재보다 이후면 유효
    }
  }

  // 토큰 유효성 검사
  public boolean validateToken(String token, String email) {
    // 이메일 일치 & 토큰 만료여부 확인
    String username = extractUsername(token);
    return (username.equals(email) && !isTokenExpired(token));
  }

  // Authentication 객체 생성
  public Authentication getAuthentication(String token) {
    String username = extractUsername(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }
}

