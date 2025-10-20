package marloncalegao.consultoday_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import marloncalegao.consultoday_api.dtos.request.PacienteRequestDTO;
import marloncalegao.consultoday_api.dtos.response.PacienteResponseDTO;
import marloncalegao.consultoday_api.service.PacienteService;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<PacienteResponseDTO> cadastrarPaciente(@RequestBody @Valid PacienteRequestDTO dados) {
            PacienteResponseDTO pacienteResponseDTO = pacienteService.cadastrarPaciente(dados);
            return ResponseEntity.ok(pacienteResponseDTO);
    }
}
