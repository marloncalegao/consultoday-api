package marloncalegao.consultoday_api.controller;

import org.springframework.data.domain.*;
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
import marloncalegao.consultoday_api.dtos.medico.MedicoListagemDTO;
import marloncalegao.consultoday_api.dtos.medico.MedicoUpdateDTO;
import marloncalegao.consultoday_api.dtos.request.MedicoRequestDTO;
import marloncalegao.consultoday_api.dtos.response.MedicoResponseDTO;
import marloncalegao.consultoday_api.service.MedicoService;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<MedicoResponseDTO> cadastrarMedico(@RequestBody @Valid MedicoRequestDTO dados) {
            MedicoResponseDTO medicoResponseDTO = medicoService.cadastrarMedico(dados);
            return ResponseEntity.ok(medicoResponseDTO);
    }
    
    @GetMapping
    public ResponseEntity<Page<MedicoListagemDTO>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao){
                Page<MedicoListagemDTO> lista = medicoService.listarMedicos(paginacao);
                return ResponseEntity.ok(lista);
    }

    @PutMapping("/atualizar/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<MedicoResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid MedicoUpdateDTO dados) {
        MedicoResponseDTO medicoAtualizado = medicoService.atualizarMedico(id, dados);
        return ResponseEntity.ok(medicoAtualizado);
    }

    @DeleteMapping("/excluir/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        medicoService.excluirMedico(id);
        return ResponseEntity.noContent().build();
    }
    
}
