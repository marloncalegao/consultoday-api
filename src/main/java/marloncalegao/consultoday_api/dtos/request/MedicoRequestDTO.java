package marloncalegao.consultoday_api.dtos.request;

import jakarta.validation.constraints.*;
import marloncalegao.consultoday_api.enums.Especialidade;

public record MedicoRequestDTO (

    @NotBlank(message = "O nome é obrigatório")
    String nome,

    @NotBlank(message = "O email é obrigatório") @Email(message = "Formato de email inválido")
    String email,

    @NotBlank(message = "O telefone é obrigatório")
    String telefone,

    @NotBlank(message = "O CRM é obrigatório")
    String crm,

    @NotBlank(message = "A senha é obrigatória")
    String senha,

    @NotNull(message = "A especialidade é obrigatória")
    Especialidade especialidade,

    boolean ativo,

    String cidade
) {
    
}
