package marloncalegao.consultoday_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import marloncalegao.consultoday_api.dtos.request.AgendamentoRequestDTO;
import marloncalegao.consultoday_api.dtos.response.AgendamentoResponseDTO;
import marloncalegao.consultoday_api.exception.ValidacaoException;
import marloncalegao.consultoday_api.model.Agendamento;
import marloncalegao.consultoday_api.model.Medico;
import marloncalegao.consultoday_api.model.Paciente;
import marloncalegao.consultoday_api.repository.AgendamentoRepository;
import marloncalegao.consultoday_api.repository.MedicoRepository;
import marloncalegao.consultoday_api.repository.PacienteRepository;
import marloncalegao.consultoday_api.validadores.ValidadorAgendamento;

@Service
public class AgendamentoService {
    private final AgendamentoRepository agendamentoRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    private final List<ValidadorAgendamento> validadores;

    public AgendamentoService(AgendamentoRepository agendamentoRepository, MedicoRepository medicoRepository, PacienteRepository pacienteRepository, List<ValidadorAgendamento> validadores) {
        this.agendamentoRepository = agendamentoRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.validadores = validadores;
    }

    private Medico buscarMedico(AgendamentoRequestDTO dados) {
        if (dados.idMedico() != null) {
            return medicoRepository.findById(dados.idMedico())
                .orElseThrow(() -> new ValidacaoException("Médico com ID fornecido não encontrado."));
        }
        
        if (dados.especialidade() == null) {
            throw new ValidacaoException("Especialidade é obrigatória quando o médico não é escolhido.");
        }
        
        return medicoRepository.escolherMedicoAleatorioLivreNaData(dados.especialidade(), dados.dataHora())
                .orElseThrow(() -> new ValidacaoException("Não há médico disponível nesta especialidade/horário."));
    }

    @Transactional
    public AgendamentoResponseDTO agendar(AgendamentoRequestDTO dados, Long idPaciente){ {
        validadores.forEach(v -> v.validar(dados, idPaciente));

        Paciente paciente = pacienteRepository.findById(idPaciente)
            .orElseThrow(() -> new ValidacaoException("Paciente não encontrado"));

        Medico medico = buscarMedico(dados);

        //if (agendamentoRepository.existsByMedicoIdAndDataHoraAndDataCancelamentoIsNull(medico.getId(), dados.dataHora())) {
        //     throw new ValidacaoException("O médico está ocupado neste horário.");
        //}
        
        Agendamento novoAgendamento = new Agendamento(medico, paciente, dados.dataHora());
        
        Agendamento agendamentoSalvo = agendamentoRepository.save(novoAgendamento);
        
        return new AgendamentoResponseDTO(agendamentoSalvo);
        }
    }
}
