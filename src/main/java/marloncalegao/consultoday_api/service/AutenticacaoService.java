package marloncalegao.consultoday_api.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import marloncalegao.consultoday_api.repository.MedicoRepository;
import marloncalegao.consultoday_api.repository.PacienteRepository;

@Service
public class AutenticacaoService implements UserDetailsService {

    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    public AutenticacaoService(MedicoRepository medicoRepository, PacienteRepository pacienteRepository) {
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails medico = medicoRepository.findByEmail(username);
        if (medico != null) {
            return medico;
        }

        UserDetails paciente = pacienteRepository.findByEmail(username);
        if (paciente != null) {
            return paciente;
        }

        throw new UsernameNotFoundException("Usuário não encontrado: " + username);

    }
    
}
