package marloncalegao.consultoday_api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import marloncalegao.consultoday_api.dtos.paciente.PacienteUpdateDTO;
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

    @GetMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'PACIENTE')")
    public ResponseEntity<Page<PacienteResponseDTO>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        Page<PacienteResponseDTO> lista = pacienteService.listarPacientes(paginacao);
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/atualizar/{id}")
    @PreAuthorize("hasRole('PACIENTE') and #id == authentication.principal.id")
    public ResponseEntity<PacienteResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid PacienteUpdateDTO dados) {
        PacienteResponseDTO pacienteAtualizado = pacienteService.atualizarPaciente(id, dados);
        return ResponseEntity.ok(pacienteAtualizado);
    }

    @DeleteMapping("/excluir/{id}")
    @PreAuthorize("hasRole('PACIENTE') and #id == authentication.principal.id")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        pacienteService.excluirPaciente(id);
        return ResponseEntity.noContent().build();
    }
}
