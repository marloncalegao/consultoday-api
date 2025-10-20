package marloncalegao.consultoday_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import marloncalegao.consultoday_api.dtos.request.PacienteRequestDTO;
import marloncalegao.consultoday_api.dtos.response.PacienteResponseDTO;
import marloncalegao.consultoday_api.exception.ValidacaoException;
import marloncalegao.consultoday_api.model.Paciente;
import marloncalegao.consultoday_api.repository.PacienteRepository;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;

    public PacienteService(PacienteRepository pacienteRepository, PasswordEncoder passwordEncoder) {
        this.pacienteRepository = pacienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public PacienteResponseDTO cadastrarPaciente(PacienteRequestDTO dados){
        if (pacienteRepository.findByEmail(dados.email()) != null) {
            throw new ValidacaoException("Email já cadastrado");
        }

        if (pacienteRepository.findByCpf(dados.cpf()) != null) {
            throw new ValidacaoException("CPF já cadastrado");
        }

        String senhaCriptografada = passwordEncoder.encode(dados.senha());

        Paciente novoPaciente = new Paciente(
            null,
            dados.nome(),
            dados.email(),
            dados.telefone(),
            dados.cpf(),
            senhaCriptografada,
            true
            );

        Paciente pacienteSalvo = pacienteRepository.save(novoPaciente);

        return new PacienteResponseDTO(pacienteSalvo);
    }
    
}
