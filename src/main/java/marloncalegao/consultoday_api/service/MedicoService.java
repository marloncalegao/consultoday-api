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
        Medico medicoExistentePorEmail = (Medico) medicoRepository.findByEmail(dados.email());
        Medico medicoExistentePorCrm = (Medico) medicoRepository.findByCrm(dados.crm());

        // Verifica duplicidade por e-mail
        if (medicoExistentePorEmail != null && medicoExistentePorEmail.getAtivo()) {
            throw new ValidacaoException("Email já cadastrado");
        }

        // Verifica duplicidade por CRM
        if (medicoExistentePorCrm != null && medicoExistentePorCrm.getAtivo()) {
            throw new ValidacaoException("CRM já cadastrado");
        }

        // Caso exista um médico com o mesmo CRM ou e-mail, mas inativo → reativa
        if (medicoExistentePorCrm != null && !medicoExistentePorCrm.getAtivo()) {
            medicoExistentePorCrm.setAtivo(true);
            medicoExistentePorCrm.setNome(dados.nome());
            medicoExistentePorCrm.setTelefone(dados.telefone());
            medicoExistentePorCrm.setEspecialidade(dados.especialidade());
            medicoExistentePorCrm.setSenha(passwordEncoder.encode(dados.senha()));
            return new MedicoResponseDTO(medicoRepository.save(medicoExistentePorCrm));
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

    @Transactional
    public void excluirMedico(Long id){
        Medico medico = medicoRepository.findById(id)
            .orElseThrow(() -> new ValidacaoException("Médico não encontrado"));

            // if (agendamentoRepository.existsByMedicoIdAndDataHoraAfter(id, LocalDateTime.now())) {
            //     throw new ValidacaoException("Médico possui agendamentos futuros e não pode ser inativado.");
            // }

            medico.setAtivo(false);
    }
    
}
