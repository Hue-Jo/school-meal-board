package com.zerobase.schoolmealboard.component.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {
//
//  // 비밀키
//  @Value("${security.jwt.secret")
//  private String secretKey;
//
//  // 만료 시간
//  private static final long TOKEN_EXPIRED_TIME = 1000 * 60 * 60; // 1시간
//
//  // 알고리즘
//  private final SignatureAlgorithm algorithm = SignatureAlgorithm.HS256;
//
//  // 토큰 생성
//  public String generateToken(String userEmail) {
//
//    return Jwts.builder()
//        .setSubject(userEmail)
//        .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRED_TIME))
//        .signWith(algorithm, secretKey)
//        .compact(); // 토큰을 문자열로 변환하여 반환
//  }
//
//  // 토큰에서 정보 추출
//  public Claims parseClaims(String token) {
//    try {
//      return Jwts.parser()
//          .setSigningKey(secretKey) // 서명을 위한 비밀키
//          .parseClaimsJws(token)  // 토큰 파싱하여 정보 추츨
//          .getBody(); // 클레임 본문 반환
//    } catch (ExpiredJwtException e) {
//      return e.getClaims();
//    }
//  }
//
//  // 토큰에서 이메일 추출
//  public String extractEmail(String token) {
//    return parseClaims(token).getSubject();
//  }
//
//  // 토큰 만료여부 확인
//  public boolean isTokenExpired(String token) {
//    // 현재시간과 비교하여 이전이면 만료
//    return parseClaims(token).getExpiration().before(new Date());
//  }
//
//  // 토큰 유효성 검사
//  public boolean validateToken(String token, String email) {
//    // 이메일 일치 & 토큰 만료여부 확인
//    return (email.equals(extractEmail(token)) && !isTokenExpired(token));
//
//  }


}
