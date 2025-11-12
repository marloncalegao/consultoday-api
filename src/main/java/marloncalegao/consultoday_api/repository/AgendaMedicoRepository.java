package marloncalegao.consultoday_api.repository;

import marloncalegao.consultoday_api.model.AgendaMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AgendaMedicoRepository extends JpaRepository<AgendaMedico, Long> {

    /**
     * Retorna todos os horários cadastrados de um médico dentro de um intervalo de tempo.
     * Usado para listar os horários já adicionados em um determinado dia.
     */
    @Query("SELECT a FROM AgendaMedico a WHERE a.medico.id = :idMedico AND a.dataHora BETWEEN :inicio AND :fim ORDER BY a.dataHora ASC")
    List<AgendaMedico> findByMedicoIdAndDataHoraBetween(Long idMedico, LocalDateTime inicio, LocalDateTime fim);

    /**
     * Retorna um horário específico de um médico, se existir.
     */
    Optional<AgendaMedico> findByMedicoIdAndDataHora(Long idMedico, LocalDateTime dataHora);

    /**
     * Verifica se já existe um horário específico registrado para o médico.
     */
    boolean existsByMedicoIdAndDataHora(Long idMedico, LocalDateTime dataHora);

}
