package marloncalegao.consultoday_api.model;

import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioAutenticado extends UserDetails {
    Long getId();
}