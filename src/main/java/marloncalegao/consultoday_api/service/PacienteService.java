package marloncalegao.consultoday_api.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import marloncalegao.consultoday_api.dtos.paciente.PacienteUpdateDTO;
import marloncalegao.consultoday_api.dtos.request.PacienteRequestDTO;
import marloncalegao.consultoday_api.dtos.response.PacienteResponseDTO;
import marloncalegao.consultoday_api.enums.StatusAgendamento;
import marloncalegao.consultoday_api.exception.ValidacaoException;
import marloncalegao.consultoday_api.model.Paciente;
import marloncalegao.consultoday_api.repository.AgendamentoRepository;
import marloncalegao.consultoday_api.repository.PacienteRepository;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final AgendamentoRepository agendamentoRepository;

    public PacienteService(PacienteRepository pacienteRepository, PasswordEncoder passwordEncoder, AgendamentoRepository agendamentoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.agendamentoRepository = agendamentoRepository;
    }

    public PacienteResponseDTO cadastrarPaciente(PacienteRequestDTO dados){
        Paciente pacienteExistentePorEmail = (Paciente) pacienteRepository.findByEmail(dados.email());
        Paciente pacienteExistentePorCpf = (Paciente) pacienteRepository.findByCpf(dados.cpf());

        if (pacienteExistentePorEmail != null && pacienteExistentePorEmail.getAtivo()) {
            throw new ValidacaoException("Email já cadastrado");
        }

        if (pacienteExistentePorCpf != null && pacienteExistentePorCpf.getAtivo()) {
            throw new ValidacaoException("CPF já cadastrado");
        }

        if (pacienteExistentePorCpf != null && !pacienteExistentePorCpf.getAtivo()) {
            pacienteExistentePorCpf.setAtivo(true);
            pacienteExistentePorCpf.setNome(dados.nome());
            pacienteExistentePorCpf.setEmail(dados.email());
            pacienteExistentePorCpf.setTelefone(dados.telefone());
            pacienteExistentePorCpf.setSenha(passwordEncoder.encode(dados.senha()));
            return new PacienteResponseDTO(pacienteRepository.save(pacienteExistentePorCpf));
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

    public Page<PacienteResponseDTO> listarPacientes(Pageable paginacao) {
        return pacienteRepository.findByAtivoTrue(paginacao).map(PacienteResponseDTO::new);
    }

    @Transactional
    public PacienteResponseDTO atualizarPaciente(Long id, PacienteUpdateDTO dados) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Paciente não encontrado com ID: " + id));

        if (dados.nome() != null && !dados.nome().isBlank()) {
            paciente.setNome(dados.nome());
        }

        if (dados.telefone() != null && !dados.telefone().isBlank()) {
            paciente.setTelefone(dados.telefone());
        }

        if (dados.email() != null && !dados.email().isBlank()) {
            paciente.setEmail(dados.email());
        }

        return new PacienteResponseDTO(paciente);
    }

    @Transactional
    public void excluirPaciente(Long id) {

        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Paciente não encontrado com ID: " + id));
        if (agendamentoRepository.existsByPacienteIdAndDataHoraAfterAndStatusNot(id, LocalDateTime.now(), StatusAgendamento.CANCELADO)) {
            throw new ValidacaoException("Paciente possui agendamentos futuros e não pode ser inativado.");
        }
        paciente.setAtivo(false);
    }

    public PacienteResponseDTO buscarPorId(Long id) {
        var paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        return new PacienteResponseDTO(paciente);
    }

}
