package marloncalegao.consultoday_api.repository;

import marloncalegao.consultoday_api.enums.Especialidade;
import marloncalegao.consultoday_api.model.Medico;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

public interface MedicoRepository extends JpaRepository<Medico, Long>{
    
    UserDetails findByEmail(String email);
    
    UserDetails findByCrm(String crm);

    Page<Medico> findByAtivoTrue(Pageable pageable);

    @Query(value = """
        SELECT * FROM medico m
        WHERE m.ativo = true
        AND m.especialidade = :especialidade
        AND m.id NOT IN (
            SELECT a.medico_id FROM agendamento a
            WHERE a.data_hora = :dataHora
            AND a.data_cancelamento IS NULL
        )
        ORDER BY RAND()
        LIMIT 1
    """, nativeQuery = true)
    Optional<Medico> escolherMedicoAleatorioLivreNaData(
        @Param("especialidade") Especialidade especialidade,
        @Param("dataHora") LocalDateTime dataHora
    );

    @Query("""
       SELECT m FROM Medico m 
       WHERE (:nome IS NULL OR LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
       AND (:especialidade IS NULL OR m.especialidade = :especialidade)
       AND (:cidade IS NULL OR LOWER(m.cidade) LIKE LOWER(CONCAT('%', :cidade, '%')))
       """)
    Page<Medico> buscarAvancado(String nome, String especialidade, String cidade, Pageable pageable);

}
