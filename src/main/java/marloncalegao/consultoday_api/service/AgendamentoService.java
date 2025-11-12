package marloncalegao.consultoday_api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import marloncalegao.consultoday_api.dtos.agendamento.AgendamentoListagemDTO;
import marloncalegao.consultoday_api.dtos.agendamento.CancelamentoAgendamentoDTO;
import marloncalegao.consultoday_api.dtos.request.AgendamentoRequestDTO;
import marloncalegao.consultoday_api.dtos.response.AgendamentoResponseDTO;
import marloncalegao.consultoday_api.enums.StatusAgendamento;
import marloncalegao.consultoday_api.exception.ValidacaoException;
import marloncalegao.consultoday_api.model.Agendamento;
import marloncalegao.consultoday_api.model.Medico;
import marloncalegao.consultoday_api.model.Paciente;
import marloncalegao.consultoday_api.repository.AgendamentoRepository;
import marloncalegao.consultoday_api.repository.MedicoRepository;
import marloncalegao.consultoday_api.repository.PacienteRepository;
import marloncalegao.consultoday_api.validadores.ValidadorAgendamento;
import marloncalegao.consultoday_api.validadores.ValidadorAntecedenciaCancelamento;

@Service
public class AgendamentoService {
    private final AgendamentoRepository agendamentoRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final ValidadorAntecedenciaCancelamento validadorCancelamento;

    private final List<ValidadorAgendamento> validadores;

    public AgendamentoService(AgendamentoRepository agendamentoRepository, MedicoRepository medicoRepository, PacienteRepository pacienteRepository, List<ValidadorAgendamento> validadores, ValidadorAntecedenciaCancelamento validadorCancelamento) {
        this.agendamentoRepository = agendamentoRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.validadores = validadores;
        this.validadorCancelamento = validadorCancelamento;
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

        Agendamento novoAgendamento = new Agendamento(medico, paciente, dados.dataHora(), StatusAgendamento.PENDENTE);

        Agendamento agendamentoSalvo = agendamentoRepository.save(novoAgendamento);

        return new AgendamentoResponseDTO(agendamentoSalvo);
        }
    }

    @Transactional
    public void cancelar(Long idAgendamento, CancelamentoAgendamentoDTO dados, Long idUsuarioLogado, String role){
        Agendamento agendamento = agendamentoRepository.findById(idAgendamento)
            .orElseThrow(() -> new ValidacaoException("Agendamento não encontrado."));

        if (agendamento.getDataCancelamento() != null) {
            throw new ValidacaoException("A consulta já foi cancelada.");
        }

        boolean isPacienteDono = agendamento.getPaciente().getId().equals(idUsuarioLogado);
        boolean isMedicoDono = agendamento.getMedico().getId().equals(idUsuarioLogado);

        if (!isPacienteDono && !isMedicoDono) {
            throw new ValidacaoException("Você não tem permissão para cancelar esta consulta.");
        }

        if (role.equals("ROLE_PACIENTE")) {
             validadorCancelamento.validar(agendamento);
        }

        agendamento.setDataCancelamento(LocalDateTime.now());
        agendamento.setMotivoCancelamento(dados.motivo());
        agendamento.setStatus(StatusAgendamento.CANCELADO);
    }

    public Page<AgendamentoListagemDTO> listarAgendamentos(Long idUsuario, String role, Pageable paginacao) {

        Page<Agendamento> agendamentos;

        if (role.equals("ROLE_MEDICO")) {
            // ✅ agora busca todas as consultas do médico
            agendamentos = agendamentoRepository.findByMedicoId(idUsuario, paginacao);

        } else if (role.equals("ROLE_PACIENTE")) {
            // ✅ agora busca todas as consultas do paciente
            agendamentos = agendamentoRepository.findByPacienteId(idUsuario, paginacao);

        } else {
            throw new ValidacaoException("Role de usuário não reconhecida para listagem de agendamentos.");
        }

        // ✅ mapeia para DTO incluindo o status (já corrigido antes)
        return agendamentos.map(AgendamentoListagemDTO::new);
    }


    public List<LocalDateTime> listarHorariosDisponiveis(Long idMedico) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime limite = agora.plusDays(7); // próximos 7 dias

        // Gera slots de 1h das 08:00 às 17:00
        List<LocalDateTime> horariosPossiveis = new java.util.ArrayList<>();
        for (int dia = 0; dia <= 6; dia++) {
            LocalDateTime data = agora.plusDays(dia).withHour(8).withMinute(0).withSecond(0).withNano(0);
            for (int hora = 8; hora <= 17; hora++) {
                horariosPossiveis.add(data.withHour(hora));
            }
        }

        // Busca agendamentos já ocupados
        List<Agendamento> ocupados = agendamentoRepository.findByMedicoIdAndDataHoraBetweenAndDataCancelamentoIsNull(
                idMedico, agora, limite);

        List<LocalDateTime> ocupadosHoras = ocupados.stream()
                .map(Agendamento::getDataHora)
                .toList();

        // Filtra horários livres
        return horariosPossiveis.stream()
                .filter(h -> !ocupadosHoras.contains(h))
                .toList();
    }

    @Transactional
    public void finalizar(Long idAgendamento, Long idMedicoLogado) {
        Agendamento agendamento = agendamentoRepository.findById(idAgendamento)
                .orElseThrow(() -> new ValidacaoException("Agendamento não encontrado."));

        if (agendamento.getStatus() == StatusAgendamento.FINALIZADO) {
            throw new ValidacaoException("Esta consulta já foi finalizada.");
        }

        if (!agendamento.getMedico().getId().equals(idMedicoLogado)) {
            throw new ValidacaoException("Você não tem permissão para finalizar esta consulta.");
        }

        agendamento.setStatus(StatusAgendamento.FINALIZADO);
        agendamento.setDataFinalizacao(LocalDateTime.now());
    }



}
