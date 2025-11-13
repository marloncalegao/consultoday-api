package marloncalegao.consultoday_api.service;

import jakarta.transaction.Transactional;
import marloncalegao.consultoday_api.dtos.agenda.AgendaSlotDTO;
import marloncalegao.consultoday_api.exception.RegraNegocioException;
import marloncalegao.consultoday_api.exception.ValidacaoException;
import marloncalegao.consultoday_api.model.AgendaMedico;
import marloncalegao.consultoday_api.model.Medico;
import marloncalegao.consultoday_api.repository.AgendaMedicoRepository;
import marloncalegao.consultoday_api.repository.MedicoRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AgendaMedicoService {

    private final AgendaMedicoRepository agendaRepository;
    private final MedicoRepository medicoRepository;


    public AgendaMedicoService(AgendaMedicoRepository agendaRepository, MedicoRepository medicoRepository) {
        this.agendaRepository = agendaRepository;
        this.medicoRepository = medicoRepository;
    }

    public boolean horarioDisponivel(Long idMedico, LocalDateTime dataHora) {
        return agendaRepository.findByMedicoIdAndDataHora(idMedico, dataHora)
                .map(AgendaMedico::isDisponivel)
                .orElse(false);
    }

    @Transactional
    public void bloquearHorario(Long idMedico, LocalDateTime dataHora) {
        Optional<AgendaMedico> opt = agendaRepository.findByMedicoIdAndDataHora(idMedico, dataHora);
        if (opt.isPresent()) {
            AgendaMedico slot = opt.get();
            if (!slot.isDisponivel()) {
                throw new RegraNegocioException("O horário já está ocupado.");
            }
            slot.setDisponivel(false);
            agendaRepository.save(slot);
        } else {
            throw new RegraNegocioException("Horário inexistente na agenda do médico.");
        }
    }

    // ===========================================================
    // LISTAR HORÁRIOS DO DIA (usado no controller)
    // ===========================================================
    public List<AgendaSlotDTO> listarSlotsPorData(Long idMedico, LocalDate data) {
        boolean diaUtil = !(data.getDayOfWeek() == DayOfWeek.SATURDAY || data.getDayOfWeek() == DayOfWeek.SUNDAY);

        List<LocalDateTime> horariosPadrao = diaUtil
                ? IntStream.rangeClosed(8, 18)
                .mapToObj(h -> LocalDateTime.of(data, LocalTime.of(h, 0)))
                .collect(Collectors.toList())
                : new ArrayList<>();

        List<AgendaMedico> horariosBanco = agendaRepository.findByMedicoIdAndDataHoraBetween(
                idMedico,
                data.atStartOfDay(),
                data.plusDays(1).atStartOfDay()
        );

        // mapa: dataHora -> disponível?
        Map<LocalDateTime, Boolean> status = horariosBanco.stream()
                .collect(Collectors.toMap(AgendaMedico::getDataHora, AgendaMedico::isDisponivel));

        List<AgendaSlotDTO> slots = new ArrayList<>();

        // ====== HORÁRIOS PADRÃO: disponível OU bloqueado ======
        for (LocalDateTime dt : horariosPadrao) {
            boolean disponivel = status.getOrDefault(dt, true);

            // SE está bloqueado → ENVIAR COMO BLOQUEADO
            if (!disponivel) {
                Optional<AgendaMedico> reg = horariosBanco.stream()
                        .filter(a -> a.getDataHora().equals(dt))
                        .findFirst();

                reg.ifPresent(agenda ->
                        slots.add(new AgendaSlotDTO(
                                agenda.getId(),
                                agenda.getDataHora(),
                                false // bloqueado
                        ))
                );
            } else {
                // disponível
                slots.add(new AgendaSlotDTO(
                        null,
                        dt,
                        true
                ));
            }
        }

        // ====== HORÁRIOS MANUAIS FORA DO RANGE ======
        horariosBanco.stream()
                .filter(a -> a.getDataHora().toLocalTime().isBefore(LocalTime.of(8, 0))
                        || a.getDataHora().toLocalTime().isAfter(LocalTime.of(18, 0)))
                .forEach(a -> slots.add(new AgendaSlotDTO(
                        a.getId(),
                        a.getDataHora(),
                        a.isDisponivel()
                )));

        // ordena
        slots.sort(Comparator.comparing(AgendaSlotDTO::dataHora));

        return slots;
    }


    // ===========================================================
    // ADICIONAR HORÁRIO MANUAL (vem disponível por padrão)
    // ===========================================================
    @Transactional
    public AgendaSlotDTO adicionarHorario(Long idMedico, LocalDateTime dataHora) {
        Medico medico = medicoRepository.findById(idMedico)
                .orElseThrow(() -> new ValidacaoException("Médico não encontrado."));

        Optional<AgendaMedico> existente = agendaRepository.findByMedicoIdAndDataHora(idMedico, dataHora);
        if (existente.isPresent()) {
            throw new ValidacaoException("Já existe um horário cadastrado para esta data/hora.");
        }

        AgendaMedico novo = new AgendaMedico();
        novo.setMedico(medico);
        novo.setDataHora(dataHora);
        novo.setDisponivel(true); // ✅ novo horário começa como disponível
        agendaRepository.save(novo);

        return new AgendaSlotDTO(novo.getId(), novo.getDataHora(), true);
    }

    // ===========================================================
    // ALTERNAR HORÁRIO (bloquear/liberar)
    // ===========================================================
    @Transactional
    public void alternarHorario(Long idMedico, LocalDateTime dataHora) {
        Medico medico = medicoRepository.findById(idMedico)
                .orElseThrow(() -> new ValidacaoException("Médico não encontrado."));

        Optional<AgendaMedico> existente = agendaRepository.findByMedicoIdAndDataHora(idMedico, dataHora);

        if (existente.isPresent()) {
            // Se existir, deleta = libera o horário
            agendaRepository.delete(existente.get());
        } else {
            // Se não existir, cria bloqueado
            AgendaMedico novo = new AgendaMedico();
            novo.setMedico(medico);
            novo.setDataHora(dataHora);
            novo.setDisponivel(false); // bloqueado
            agendaRepository.save(novo);
        }
    }

    // ===========================================================
    // REMOVER HORÁRIO POR DATA (usado no DELETE ?dataHora=)
    // ===========================================================
    @Transactional
    public void removerHorario(Long idMedico, LocalDateTime dataHora) {
        Optional<AgendaMedico> existente = agendaRepository.findByMedicoIdAndDataHora(idMedico, dataHora);
        if (existente.isEmpty()) {
            throw new ValidacaoException("Horário não encontrado.");
        }
        agendaRepository.delete(existente.get());
    }

    // ===========================================================
    // REMOVER HORÁRIO POR ID (usado no DELETE /{id})
    // ===========================================================
    @Transactional
    public void removerPorId(Long idHorario, Long idMedico) {
        Optional<AgendaMedico> existente = agendaRepository.findById(idHorario);
        if (existente.isEmpty()) {
            throw new ValidacaoException("Horário não encontrado.");
        }

        AgendaMedico horario = existente.get();
        if (!Objects.equals(horario.getMedico().getId(), idMedico)) {
            throw new ValidacaoException("Este horário não pertence ao médico logado.");
        }

        agendaRepository.delete(horario);
    }


}
