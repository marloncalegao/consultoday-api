package marloncalegao.consultoday_api.controller;

import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import marloncalegao.consultoday_api.dtos.agendamento.AgendamentoListagemDTO;
import marloncalegao.consultoday_api.dtos.agendamento.CancelamentoAgendamentoDTO;
import marloncalegao.consultoday_api.dtos.request.AgendamentoRequestDTO;
import marloncalegao.consultoday_api.dtos.response.AgendamentoResponseDTO;
import marloncalegao.consultoday_api.model.UsuarioAutenticado;
import marloncalegao.consultoday_api.service.AgendamentoService;

@RestController
@RequestMapping("/api/consultas")
public class AgendamentoController {
    
    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    private Long getAuthenticatedUserId(UsuarioAutenticado usuario) {
        return usuario.getId();
    }

    private String getAuthenticatedUserRole(UsuarioAutenticado usuario) {
        return usuario.getAuthorities().iterator().next().getAuthority();
    }

    @PostMapping ("/agendar")
    @PreAuthorize("hasRole('PACIENTE')")
    @Transactional
    public ResponseEntity<AgendamentoResponseDTO> agendar(
        @RequestBody @Valid AgendamentoRequestDTO dados,
        @AuthenticationPrincipal UsuarioAutenticado usuarioLogado 
    ) {
        Long idPaciente = ((marloncalegao.consultoday_api.model.Paciente) usuarioLogado).getId();
        
        AgendamentoResponseDTO agendamento = agendamentoService.agendar(dados, idPaciente);

        return ResponseEntity.status(HttpStatus.CREATED).body(agendamento);
    }

    @DeleteMapping ("/cancelar/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'MEDICO')")
    public ResponseEntity<AgendamentoResponseDTO> cancelar(
        @PathVariable Long id, 
        @RequestBody @Valid CancelamentoAgendamentoDTO dados,
        @AuthenticationPrincipal UsuarioAutenticado usuarioLogado
    ) {
        Long idUsuario = ((marloncalegao.consultoday_api.model.Paciente) usuarioLogado).getId();
        String role = usuarioLogado.getAuthorities().iterator().next().getAuthority();

        agendamentoService.cancelar(id, dados, idUsuario, role);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<AgendamentoListagemDTO>> listar(
            @PageableDefault(size = 10, sort = {"dataHora"}) Pageable paginacao,
            @AuthenticationPrincipal UsuarioAutenticado usuarioLogado
    ) {
        Long idUsuario = usuarioLogado.getId();
        String role = usuarioLogado.getAuthorities().iterator().next().getAuthority();

        Page<AgendamentoListagemDTO> lista = agendamentoService.listarAgendamentos(idUsuario, role, paginacao);
        return ResponseEntity.ok(lista);
    }
}
