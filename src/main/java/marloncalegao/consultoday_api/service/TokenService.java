package marloncalegao.consultoday_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

@Service
public class TokenService {
    
    @Value("${consultoday.api.security.token.secret}")
    private String secret;

    public String gerarToken(UserDetails user){
        try{
            var algoritmo = Algorithm.HMAC256(secret);

            return JWT.create()
                .withIssuer("Consultoday API")
                .withSubject(user.getUsername())    
                .sign(algoritmo);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String getSubject(String tokenJWT){
        try{
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                .withIssuer("Consultoday API")
                .build()
                .verify(tokenJWT)
                .getSubject();
        } catch (Exception exception){
            throw new RuntimeException("Token JWT inválido", exception);
        }
    }
}
