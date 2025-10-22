package marloncalegao.consultoday_api.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import marloncalegao.consultoday_api.repository.MedicoRepository;
import marloncalegao.consultoday_api.repository.PacienteRepository;
import marloncalegao.consultoday_api.service.TokenService;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    public SecurityFilter(TokenService tokenService, MedicoRepository medicoRepository, PacienteRepository pacienteRepository) {
        this.tokenService = tokenService;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            var subject = tokenService.getSubject(tokenJWT);
            
            UserDetails usuario = medicoRepository.findByEmail(subject);
            
            if (usuario == null) {
                 usuario = pacienteRepository.findByEmail(subject);
            }
            
            var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
    
}
