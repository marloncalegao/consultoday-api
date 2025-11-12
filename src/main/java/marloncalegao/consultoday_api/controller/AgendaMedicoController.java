package marloncalegao.consultoday_api.controller;

import jakarta.validation.Valid;
import marloncalegao.consultoday_api.dtos.agenda.AgendaSlotDTO;
import marloncalegao.consultoday_api.model.UsuarioAutenticado;
import marloncalegao.consultoday_api.service.AgendaMedicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agenda-medico")
public class AgendaMedicoController {

    private final AgendaMedicoService agendaService;

    public AgendaMedicoController(AgendaMedicoService agendaService) {
        this.agendaService = agendaService;
    }

    // ===============================
    // LISTAR HORÁRIOS DO DIA
    // ===============================
    @GetMapping
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ROLE_MEDICO')")
    public ResponseEntity<List<AgendaSlotDTO>> listarAgenda(
            @AuthenticationPrincipal UsuarioAutenticado usuarioLogado,
            @RequestParam("date") String dateIso) {

        LocalDate data = LocalDate.parse(dateIso);
        List<AgendaSlotDTO> slots = agendaService.listarSlotsPorData(usuarioLogado.getId(), data);
        return ResponseEntity.ok(slots);
    }

    // ===============================
    // ADICIONAR NOVO HORÁRIO
    // ===============================
    @PostMapping
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ROLE_MEDICO')")
    public ResponseEntity<AgendaSlotDTO> adicionarHorario(
            @AuthenticationPrincipal UsuarioAutenticado usuarioLogado,
            @RequestBody @Valid Map<String, String> body) {

        LocalDateTime dataHora = LocalDateTime.parse(body.get("dataHora"));
        AgendaSlotDTO slot = agendaService.adicionarHorario(usuarioLogado.getId(), dataHora);
        return ResponseEntity.ok(slot);
    }

    // ===============================
    // REMOVER HORÁRIO POR DATA
    // ===============================
    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ROLE_MEDICO')")
    public ResponseEntity<Void> removerHorarioPorData(
            @AuthenticationPrincipal UsuarioAutenticado usuarioLogado,
            @RequestParam("dataHora") String dataHoraIso) {

        LocalDateTime dataHora = LocalDateTime.parse(dataHoraIso);
        agendaService.removerHorario(usuarioLogado.getId(), dataHora);
        return ResponseEntity.noContent().build();
    }

    // ===============================
    // REMOVER HORÁRIO POR ID
    // ===============================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ROLE_MEDICO')")
    public ResponseEntity<Void> removerHorarioPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado usuarioLogado) {

        agendaService.removerPorId(id, usuarioLogado.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ROLE_MEDICO')")
    public ResponseEntity<Void> alternarHorario(
            @AuthenticationPrincipal UsuarioAutenticado usuarioLogado,
            @RequestBody Map<String, String> body) {

        LocalDateTime dataHora = LocalDateTime.parse(body.get("dataHora"));
        agendaService.alternarHorario(usuarioLogado.getId(), dataHora);
        return ResponseEntity.noContent().build();
    }

}
