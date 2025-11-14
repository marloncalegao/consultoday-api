package marloncalegao.consultoday_api.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import marloncalegao.consultoday_api.model.AgendaMedico;
import marloncalegao.consultoday_api.repository.AgendaMedicoRepository;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AgendamentoService {
    private final AgendamentoRepository agendamentoRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final ValidadorAntecedenciaCancelamento validadorCancelamento;
    private final AgendaMedicoRepository agendaMedicoRepository;

    private final List<ValidadorAgendamento> validadores;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                              MedicoRepository medicoRepository,
                              PacienteRepository pacienteRepository,
                              List<ValidadorAgendamento> validadores,
                              ValidadorAntecedenciaCancelamento validadorCancelamento,
                              AgendaMedicoRepository agendaMedicoRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.validadores = validadores;
        this.validadorCancelamento = validadorCancelamento;
        this.agendaMedicoRepository = agendaMedicoRepository;
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

        boolean horarioOcupado = agendamentoRepository
                .existsByMedicoIdAndDataHoraAndStatusIn(
                        medico.getId(),
                        dados.dataHora(),
                        List.of(StatusAgendamento.AGENDADO, StatusAgendamento.PENDENTE)
                );

        if (horarioOcupado) {
            throw new ValidacaoException("O médico está ocupado neste horário.");
        }


        Agendamento novoAgendamento = new Agendamento(medico, paciente, dados.dataHora(), StatusAgendamento.AGENDADO);

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
        LocalDateTime agora = LocalDateTime.now().withSecond(0).withNano(0);
        LocalDate inicio = agora.toLocalDate();
        LocalDate fim = inicio.plusDays(6); // próximos 7 dias (inclui hoje)

        LocalDateTime inicioRange = inicio.atStartOfDay();
        LocalDateTime fimRange = fim.plusDays(1).atStartOfDay();


        List<Agendamento> ocupados = agendamentoRepository
                .findByMedicoIdAndDataHoraBetweenAndDataCancelamentoIsNull(idMedico, inicioRange, fimRange)
                .stream()
                .filter(a -> a.getStatus() == StatusAgendamento.AGENDADO
                        || a.getStatus() == StatusAgendamento.PENDENTE)
                .collect(Collectors.toList());


        Set<LocalDateTime> ocupadosSet = ocupados.stream()
                .map(Agendamento::getDataHora)
                .collect(Collectors.toSet());

        // 2) buscar todos os registros da agenda (bloqueios/manuais) no período
        List<AgendaMedico> registrosAgenda = agendaMedicoRepository
                .findByMedicoIdAndDataHoraBetween(idMedico, inicioRange, fimRange);

        // Considera bloqueados (disponivel == false) apenas — outros (disponivel==true) são horários manuais disponíveis
        Set<LocalDateTime> bloqueadosSet = registrosAgenda.stream()
                .filter(a -> !a.isDisponivel())
                .map(AgendaMedico::getDataHora)
                .collect(Collectors.toSet());

        // Também monte um set de horários manuais que estão disponíveis (fora do padrão)
        Set<LocalDateTime> manuaisDisponiveis = registrosAgenda.stream()
                .filter(AgendaMedico::isDisponivel)
                .map(AgendaMedico::getDataHora)
                .collect(Collectors.toSet());

        List<LocalDateTime> resultado = new ArrayList<>();

        // Para cada dia do período
        for (int d = 0; d <= 6; d++) {
            LocalDate dia = inicio.plusDays(d);

            // pular finais de semana, se essa for a regra do seu app (sua AgendaMedicoService faz isso)
            if (dia.getDayOfWeek() == DayOfWeek.SATURDAY || dia.getDayOfWeek() == DayOfWeek.SUNDAY) {
                // ainda assim incluir quaisquer horários manuais disponíveis desse dia
                final LocalDate diaFinal = dia;
                manuaisDisponiveis.stream()
                        .filter(dt -> dt.toLocalDate().equals(diaFinal))
                        .forEach(resultado::add);
                continue;
            }

            // Gerar horários padrão 08:00..18:00 (consistência com AgendaMedicoService)
            for (int hora = 8; hora <= 18; hora++) {
                LocalDateTime slot = LocalDateTime.of(dia, LocalTime.of(hora, 0));

                // remover se ocupado por agendamento
                if (ocupadosSet.contains(slot)) continue;

                // remover se bloqueado explicitamente pelo médico
                if (bloqueadosSet.contains(slot)) continue;

                // se existe registro manual disponível (AgendaMedico com disponivel=true) também considerar disponível,
                // mas isso já não é necessário aqui porque se há registro com disponivel=true ele não estará no bloqueadosSet/ocupadosSet
                resultado.add(slot);
            }

            // incluir horários manuais desse dia (fora do padrão) que estejam disponiveis e não ocupados
            final LocalDate diaFinal2 = dia;
            manuaisDisponiveis.stream()
                    .filter(dt -> dt.toLocalDate().equals(diaFinal2))
                    .filter(dt -> !ocupadosSet.contains(dt))
                    .forEach(resultado::add);
        }

        // Ordena e remove slots no passado (caso hoje inclua horas passadas)
        LocalDateTime agoraTrunc = LocalDateTime.now().withSecond(0).withNano(0);
        List<LocalDateTime> ordenado = resultado.stream()
                .filter(dt -> !dt.isBefore(agoraTrunc))
                .sorted()
                .collect(Collectors.toList());

        return ordenado;
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
