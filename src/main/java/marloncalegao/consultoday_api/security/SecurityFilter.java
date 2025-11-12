package marloncalegao.consultoday_api.security;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import marloncalegao.consultoday_api.model.UsuarioAutenticado;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Permitir pré-voo (CORS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            String email = tokenService.getSubject(token);
            String role = tokenService.getRole(token);

            // Corrige caso o role venha sem prefixo
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }

            UsuarioAutenticado usuario = buscarUsuario(email);
            if (usuario != null) {
                var authority = new SimpleGrantedAuthority(role);
                var authentication = new UsernamePasswordAuthenticationToken(
                        usuario, null, List.of(authority));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            System.out.println("Erro ao autenticar token: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private UsuarioAutenticado buscarUsuario(String email) {
        var medico = medicoRepository.findByEmail(email);
        if (medico != null) {
            return (UsuarioAutenticado) medico;
        }

        var paciente = pacienteRepository.findByEmail(email);
        if (paciente != null) {
            return (UsuarioAutenticado) paciente;
        }

        return null;
    }
}
