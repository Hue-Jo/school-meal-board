package com.zerobase.schoolmealboard.component.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  @Autowired
  private UserDetailsService userDetailsService;

  public static final String TOKEN_HEADER = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // 요청에서 JWT 추출
    String token = extractTokenFromRequest(request);

    if (StringUtils.hasText(token) && jwtUtil.validateToken(token,
        jwtUtil.extractUsername(token))) {
      // 유효한 토큰일 경우, Authentication 객체를 생성하여 SecurityContext에 설정
      Authentication auth = jwtUtil.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    filterChain.doFilter(request, response);
  }

  // 요청의 헤더에서 토큰을 추출하는 메서드
  private String extractTokenFromRequest(HttpServletRequest request) {

    String token = request.getHeader(TOKEN_HEADER);

    if (StringUtils.hasText(token) && token.startsWith(TOKEN_PREFIX)) {
      // "Bearer " 부분을 제외하고 토큰만 반환
      return token.substring(TOKEN_PREFIX.length());
    }
    // 토큰이 존재하지 않거나, "Bearer "로 시작하지 않는 경우 null 반환
    return null;
  }
}


