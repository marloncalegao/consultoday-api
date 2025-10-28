package marloncalegao.consultoday_api.controller;

import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import marloncalegao.consultoday_api.dtos.MedicoListagemDTO;
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
    
}
