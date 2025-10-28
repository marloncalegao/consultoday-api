package marloncalegao.consultoday_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import marloncalegao.consultoday_api.dtos.MedicoListagemDTO;
import marloncalegao.consultoday_api.dtos.MedicoUpdateDTO;
import marloncalegao.consultoday_api.dtos.request.MedicoRequestDTO;
import marloncalegao.consultoday_api.dtos.response.MedicoResponseDTO;
import marloncalegao.consultoday_api.exception.ValidacaoException;
import marloncalegao.consultoday_api.model.Medico;
import marloncalegao.consultoday_api.repository.MedicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final PasswordEncoder passwordEncoder;

    public MedicoService(MedicoRepository medicoRepository, PasswordEncoder passwordEncoder) {
        this.medicoRepository = medicoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public MedicoResponseDTO cadastrarMedico(MedicoRequestDTO dados){
        if (medicoRepository.findByEmail(dados.email()) != null) {
            throw new ValidacaoException("Email já cadastrado");
        }

        if (medicoRepository.findByCrm(dados.crm()) != null) {
            throw new ValidacaoException("CRM já cadastrado");
        }

        String senhaCriptografada = passwordEncoder.encode(dados.senha());

        Medico novoMedico = new Medico(
            null,
            dados.nome(),
            dados.email(),
            dados.telefone(),
            dados.crm(),
            senhaCriptografada,
            true,
            dados.especialidade()
            );

        Medico medicoSalvo = medicoRepository.save(novoMedico);

        return new MedicoResponseDTO(medicoSalvo);
    }

    public Page<MedicoListagemDTO> listarMedicos(Pageable pageable) {
        return medicoRepository.findByAtivoTrue(pageable).map(MedicoListagemDTO::new);
    }

    @Transactional
    public MedicoResponseDTO atualizarMedico(Long id, MedicoUpdateDTO dados){
        Medico medico = medicoRepository.findById(id)
            .orElseThrow(() -> new ValidacaoException("Médico não encontrado"));

        if (dados.nome() != null && !dados.nome().isBlank()) {
            medico.setNome(dados.nome());
        }

        if (dados.telefone() != null && !dados.telefone().isBlank()) {
            medico.setTelefone(dados.telefone());
        }

        if (dados.email() != null && !dados.email().isBlank()) {
            medico.setEmail(dados.email());
        }

        return new MedicoResponseDTO(medico);
    }
    
}
