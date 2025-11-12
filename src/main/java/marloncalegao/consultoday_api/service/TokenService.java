package marloncalegao.consultoday_api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import marloncalegao.consultoday_api.model.UsuarioAutenticado;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${consultoday.api.security.token.secret}")
    private String secret;

    public String gerarToken(UsuarioAutenticado usuario) {
        try {
            String role = usuario.getAuthorities().iterator().next().getAuthority();

            // 🔹 Garante o prefixo ROLE_
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }

            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("Consultoday API")
                    .withSubject(usuario.getUsername())
                    .withClaim("role", role)
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String getSubject(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("Consultoday API")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado!");
        }
    }

    // 🔹 NOVO: método que extrai o papel do usuário (role)
    public String getRole(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("Consultoday API")
                    .build()
                    .verify(token)
                    .getClaim("role")
                    .asString();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Erro ao obter role do token!");
        }
    }

}
