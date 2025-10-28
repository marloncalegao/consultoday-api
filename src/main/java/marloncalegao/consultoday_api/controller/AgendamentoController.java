package marloncalegao.consultoday_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import marloncalegao.consultoday_api.dtos.request.AgendamentoRequestDTO;
import marloncalegao.consultoday_api.dtos.response.AgendamentoResponseDTO;
import marloncalegao.consultoday_api.service.AgendamentoService;

@RestController
@RequestMapping("/api/consultas")
public class AgendamentoController {
    
    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping ("/agendar")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<AgendamentoResponseDTO> agendar(
        @RequestBody @Valid AgendamentoRequestDTO dados,
        @AuthenticationPrincipal UserDetails pacienteLogado 
    ) {
        Long idPaciente = ((marloncalegao.consultoday_api.model.Paciente) pacienteLogado).getId();
        
        AgendamentoResponseDTO agendamento = agendamentoService.agendar(dados, idPaciente);

        return ResponseEntity.status(HttpStatus.CREATED).body(agendamento);
    }
}
