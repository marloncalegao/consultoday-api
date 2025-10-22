package marloncalegao.consultoday_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import marloncalegao.consultoday_api.dtos.DadosLoginDTO;
import marloncalegao.consultoday_api.dtos.DadosTokenJWT;
import marloncalegao.consultoday_api.service.TokenService;


@RestController
@RequestMapping("/auth")
public class AutenticacaoController {
    
    private final AuthenticationManager manager;
    private final TokenService tokenService;

    public AutenticacaoController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.manager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<DadosTokenJWT> efetuarLogin(@RequestBody @Valid DadosLoginDTO dados) {
        var token = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

        Authentication autenticacao = manager.authenticate(token);

        var TokenJWT = tokenService.gerarToken((org.springframework.security.core.userdetails.UserDetails) autenticacao.getPrincipal());

        return ResponseEntity.ok(new DadosTokenJWT(TokenJWT));
    } 
}
