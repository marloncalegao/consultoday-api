package marloncalegao.consultoday_api.dtos.response;

public record DadosLoginResponse(
        String token,
        Long id,
        String nome,
        String tipoUsuario
) {}
