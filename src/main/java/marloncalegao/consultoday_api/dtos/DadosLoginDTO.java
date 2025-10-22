package marloncalegao.consultoday_api.dtos;

import jakarta.validation.constraints.NotBlank;

public record DadosLoginDTO (
    @NotBlank String email,
    @NotBlank String senha
) {
    
}
