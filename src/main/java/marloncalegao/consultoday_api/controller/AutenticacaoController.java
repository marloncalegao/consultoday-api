package marloncalegao.consultoday_api.controller;

import jakarta.validation.Valid;
import marloncalegao.consultoday_api.dtos.DadosLoginDTO;
import marloncalegao.consultoday_api.dtos.response.DadosLoginResponse;
import marloncalegao.consultoday_api.model.UsuarioAutenticado;
import marloncalegao.consultoday_api.repository.MedicoRepository;
import marloncalegao.consultoday_api.repository.PacienteRepository;
import marloncalegao.consultoday_api.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    private final AuthenticationManager manager;
    private final TokenService tokenService;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    public AutenticacaoController(
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            MedicoRepository medicoRepository,
            PacienteRepository pacienteRepository
    ) {
        this.manager = authenticationManager;
        this.tokenService = tokenService;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<DadosLoginResponse> efetuarLogin(@RequestBody @Valid DadosLoginDTO dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        Authentication autenticacao = manager.authenticate(authenticationToken);

        var usuarioAutenticado = (UsuarioAutenticado) autenticacao.getPrincipal();
        var tokenJWT = tokenService.gerarToken(usuarioAutenticado);

        String tipoUsuario;
        if (usuarioAutenticado instanceof marloncalegao.consultoday_api.model.Medico) {
            tipoUsuario = "MEDICO";
        } else if (usuarioAutenticado instanceof marloncalegao.consultoday_api.model.Paciente) {
            tipoUsuario = "PACIENTE";
        } else {
            tipoUsuario = "USUARIO";
        }

        var response = new DadosLoginResponse(
                tokenJWT,
                usuarioAutenticado.getId(),
                usuarioAutenticado.getNome(),
                tipoUsuario
        );

        return ResponseEntity.ok(response);
    }
}
