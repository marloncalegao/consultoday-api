package marloncalegao.consultoday_api.controller;

import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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

import java.time.LocalDateTime;
import java.util.List;

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

    // ============================
    // AGENDAR CONSULTA (PACIENTE)
    // ============================
    @PostMapping("/agendar")
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_PACIENTE')")
    public ResponseEntity<AgendamentoResponseDTO> agendar(
            @RequestBody @Valid AgendamentoRequestDTO dados,
            @AuthenticationPrincipal UsuarioAutenticado usuarioLogado) {

        Long idPaciente = getAuthenticatedUserId(usuarioLogado);
        AgendamentoResponseDTO agendamento = agendamentoService.agendar(dados, idPaciente);

        return ResponseEntity.status(HttpStatus.CREATED).body(agendamento);
    }

    // ===================================
    // CANCELAR CONSULTA (PACIENTE/MÉDICO)
    // ===================================
    @DeleteMapping("/cancelar/{id}")
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_PACIENTE', 'ROLE_MEDICO')")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long id,
            @RequestBody @Valid CancelamentoAgendamentoDTO dados,
            @AuthenticationPrincipal UsuarioAutenticado usuarioLogado) {

        Long idUsuario = getAuthenticatedUserId(usuarioLogado);
        String role = getAuthenticatedUserRole(usuarioLogado);

        agendamentoService.cancelar(id, dados, idUsuario, role);

        return ResponseEntity.noContent().build();
    }

    // ===================================
    // LISTAR CONSULTAS (PACIENTE/MÉDICO)
    // ===================================
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_PACIENTE', 'ROLE_MEDICO')")
    public ResponseEntity<Page<AgendamentoListagemDTO>> listar(
            @PageableDefault(size = 10, sort = {"dataHora"}, direction = Sort.Direction.DESC) Pageable paginacao,
            @AuthenticationPrincipal UsuarioAutenticado usuarioLogado) {

        Long idUsuario = getAuthenticatedUserId(usuarioLogado);
        String role = getAuthenticatedUserRole(usuarioLogado);

        Page<AgendamentoListagemDTO> lista = agendamentoService.listarAgendamentos(idUsuario, role, paginacao);
        System.out.println("Usuário logado: " + usuarioLogado);
        System.out.println("Authorities do usuário logado: " + usuarioLogado.getAuthorities());
        return ResponseEntity.ok(lista);

    }

    // ============================
    // HORÁRIOS DISPONÍVEIS (MÉDICO)
    // ============================
    @GetMapping("/disponiveis/{idMedico}")
    @PreAuthorize("hasAnyAuthority('ROLE_PACIENTE', 'ROLE_MEDICO')")
    public ResponseEntity<List<LocalDateTime>> listarHorariosDisponiveis(@PathVariable Long idMedico) {
        List<LocalDateTime> horarios = agendamentoService.listarHorariosDisponiveis(idMedico);
        return ResponseEntity.ok(horarios);
    }

    // ============================
    // FINALIZAR CONSULTA (MÉDICO)
    // ============================
    @PutMapping("/finalizar/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<Void> finalizar(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado usuarioLogado) {

        agendamentoService.finalizar(id, usuarioLogado.getId());
        return ResponseEntity.noContent().build();
    }

}
