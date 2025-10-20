package marloncalegao.consultoday_api.repository;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import marloncalegao.consultoday_api.model.Agendamento;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    boolean existsByMedicoIdAndDataHora(Long medicoId, LocalDateTime dataHora);

    boolean existsByPacienteIdAndDataHora(Long pacienteId, LocalDateTime dataHora);
}
