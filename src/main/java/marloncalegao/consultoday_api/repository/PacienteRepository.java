package marloncalegao.consultoday_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import marloncalegao.consultoday_api.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long>{

    UserDetails findByEmail(String email);

    UserDetails findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    Page<Paciente> findByAtivoTrue(Pageable paginacao);
} 
