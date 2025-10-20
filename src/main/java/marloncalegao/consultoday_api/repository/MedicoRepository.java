package marloncalegao.consultoday_api.repository;

import marloncalegao.consultoday_api.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface MedicoRepository extends JpaRepository<Medico, Long>{
    
    UserDetails findByEmail(String email);
    
    UserDetails findByCrm(String crm);
}
