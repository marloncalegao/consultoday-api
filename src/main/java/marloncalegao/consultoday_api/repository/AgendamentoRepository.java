package marloncalegao.consultoday_api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import marloncalegao.consultoday_api.enums.StatusAgendamento;
import marloncalegao.consultoday_api.model.Agendamento;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    boolean existsByMedicoIdAndDataHora(Long medicoId, LocalDateTime dataHora);

    boolean existsByPacienteIdAndDataHora(Long pacienteId, LocalDateTime dataHora);

    Page<Agendamento> findByMedicoIdAndStatusNot(Long idMedico, StatusAgendamento statusExcluido, Pageable paginacao);

    Page<Agendamento> findByPacienteIdAndStatusNot(Long idPaciente, StatusAgendamento statusExcluido, Pageable paginacao);

    boolean existsByPacienteIdAndDataHoraBetweenAndStatusNot(Long idPaciente, LocalDateTime inicioDoDia, LocalDateTime fimDoDia, StatusAgendamento statusExcluido);

    boolean existsByPacienteIdAndDataHoraAfterAndStatusNot(Long idPaciente, LocalDateTime now, StatusAgendamento statusExcluido);

    List<Agendamento> findByMedicoIdAndDataHoraBetweenAndDataCancelamentoIsNull(
            Long idMedico, LocalDateTime inicio, LocalDateTime fim);

    Page<Agendamento> findByMedicoId(Long idMedico, Pageable paginacao);

    Page<Agendamento> findByPacienteId(Long idPaciente, Pageable paginacao);

    @Query("SELECT COUNT(a) > 0 FROM Agendamento a " +
            "WHERE a.medico.id = :idMedico " +
            "AND a.dataHora = :dataHora " +
            "AND a.status <> 'CANCELADO'")
    boolean existsByMedicoAndDataHora(@Param("idMedico") Long idMedico,
                                      @Param("dataHora") LocalDateTime dataHora);
}

