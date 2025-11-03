package marloncalegao.consultoday_api.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import marloncalegao.consultoday_api.enums.StatusAgendamento;
import marloncalegao.consultoday_api.model.Agendamento;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    boolean existsByMedicoIdAndDataHora(Long medicoId, LocalDateTime dataHora);

    boolean existsByPacienteIdAndDataHora(Long pacienteId, LocalDateTime dataHora);

    Page<Agendamento> findByMedicoIdAndStatusNot(Long idMedico, StatusAgendamento statusExcluido, Pageable paginacao);
    
    Page<Agendamento> findByPacienteIdAndStatusNot(Long idPaciente, StatusAgendamento statusExcluido, Pageable paginacao);
}
