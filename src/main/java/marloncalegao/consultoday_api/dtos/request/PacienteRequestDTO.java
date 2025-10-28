package marloncalegao.consultoday_api.dtos.request;

import jakarta.validation.constraints.*;

public record PacienteRequestDTO(

    @NotBlank(message = "O nome é obrigatório")
    String nome,

    @NotBlank(message = "O email é obrigatório") @Email(message = "Formato de email inválido")
    String email,

    @NotBlank(message = "O telefone é obrigatório")
    String telefone,

    @NotBlank(message = "O CPF é obrigatório") @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 dígitos")
    String cpf,

    @NotBlank(message = "A senha é obrigatória")
    String senha,

    boolean ativo
) {

}
