package marloncalegao.consultoday_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import marloncalegao.consultoday_api.dtos.request.MedicoRequestDTO;
import marloncalegao.consultoday_api.dtos.response.MedicoResponseDTO;
import marloncalegao.consultoday_api.exception.ValidacaoException;
import marloncalegao.consultoday_api.model.Medico;
import marloncalegao.consultoday_api.repository.MedicoRepository;

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
    
}
