package edu.classroom.board;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HexFormat;

@Service
public class AuthService {

  // application.properties의 app.jwt.* 설정값을 주입하기
  @Value("${app.jwt.secret}")
  private String secretHex;

  @Value("${app.jwt.issuer:webservice-class}")
  private String issuer;

  @Value("${app.jwt.audience:board-api}")
  private String audience;

  @Value("${app.jwt.ttl:90}")
  private long tokenTtl;

  // 64자리 16진수(32바이트) 시크릿 키를 HS256 서명 SecretKey로 변환
  private SecretKey getSecretKey() {
    if (secretHex == null || secretHex.length() != 64) {
      throw new IllegalArgumentException("JWT_SECRET은 64자리 16진수 문자열이어야 합니다.");
    }
    byte[] keyBytes = HexFormat.of().parseHex(secretHex);
    if (keyBytes.length != 32) {
      throw new IllegalArgumentException("JWT_SECRET은 32바이트 키여야 합니다.");
    }
    return Keys.hmacShaKeyFor(keyBytes);
  }

  // JJWT 의존성은 pom.xml에 포함되어 있습니다. app.jwt.* 설정은 application.properties 참조.
  public String issueToken(Users.PublicUser user) {
    // TODO BE-01: 서버 키로 HS256 JWT 발급. sub, exp, iss, aud 필수.
    // 명세/체크리스트 요구사항: HS256 고정, sub=사용자ID, exp=만료시간, iss, aud 반영
    if (user == null) {
      throw new ApiError(401, "INVALID_CREDENTIALS", "사용자 정보가 없습니다.");
    }

    Date now = new Date();
    Date expiry = new Date(now.getTime() + tokenTtl * 1000);

    return Jwts.builder()
            .subject(String.valueOf(user.id()))
            .issuer(issuer)
            .audience().add(audience).and()
            .issuedAt(now)
            .expiration(expiry)
            .signWith(getSecretKey(), Jwts.SIG.HS256)
            .compact();
  }

  public long currentUserId(HttpServletRequest request) {
    // TODO BE-02: Bearer 추출 → 서명/알고리즘/만료/클레임 검증 → 실제 사용자 확인.
    // 완성 후 아래 데모 ID 반환을 제거하세요. 토큰 없음/변조/만료는 401입니다.
    // 명세/체크리스트 요구사항: Authorization 헤더 검증 및 토큰 서명·만료·클레임 엄격 검증
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      throw new ApiError(401, "TOKEN_REQUIRED", "토큰이 없거나 형식이 잘못되었습니다.");
    }

    String token = header.substring(7).trim();
    if (token.isEmpty()) {
      throw new ApiError(401, "TOKEN_REQUIRED", "토큰이 비어있습니다.");
    }

    try {
      Claims claims = Jwts.parser()
              .verifyWith(getSecretKey())
              .build()
              .parseSignedClaims(token)
              .getPayload();

      if (!issuer.equals(claims.getIssuer()) || !claims.getAudience().contains(audience)) {
        throw new ApiError(401, "INVALID_TOKEN", "토큰의 클레임 정보가 올바르지 않습니다.");
      }

      String sub = claims.getSubject();
      if (sub == null || sub.isBlank()) {
        throw new ApiError(401, "INVALID_TOKEN", "사용자 ID가 존재하지 않습니다.");
      }

      return Long.parseLong(sub);

    } catch (ExpiredJwtException e) {
      throw new ApiError(401, "TOKEN_EXPIRED", "로그인 시간이 만료되었습니다. 다시 로그인하세요.");
    } catch (JwtException | NumberFormatException e) {
      throw new ApiError(401, "INVALID_TOKEN", "유효하지 않은 토큰입니다.");
    }
  }

  public void assertOwner(long userId, long authorId) {
    // TODO BE-03: 다른 작성자이면 403 FORBIDDEN. 현재는 검사하지 않습니다.
    // 명세/체크리스트 요구사항: 로그인 사용자와 글 작성자 ID가 다를 경우 403 FORBIDDEN 발생
    if (userId != authorId) {
      throw new ApiError(403, "FORBIDDEN", "본인이 작성한 글만 수정 및 삭제할 수 있습니다.");
    }
  }
}