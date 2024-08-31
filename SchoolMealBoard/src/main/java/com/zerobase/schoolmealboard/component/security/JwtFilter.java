package com.zerobase.schoolmealboard.component.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter  {
//
//  private final JwtUtil jwtUtil;
//  public static final String TOKEN_HEADER = "Authorization";
//  public static final String TOKEN_PREFIX = "Bearer ";
//
//
//  @Override
//  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//      FilterChain filterChain) throws ServletException, IOException {
//
//    String authHeader = request.getHeader(TOKEN_HEADER);
//    String email = null;
//    String token = null;
//
//    if (StringUtils.hasText(authHeader) && authHeader.startsWith(TOKEN_PREFIX)) {
//      token = authHeader.substring(TOKEN_PREFIX.length());  // jwt 토큰만 추출
//      email = jwtUtil.extractEmail(token);  // jwt에서 이메일 추출
//    }
//
//
//  }
//

}
