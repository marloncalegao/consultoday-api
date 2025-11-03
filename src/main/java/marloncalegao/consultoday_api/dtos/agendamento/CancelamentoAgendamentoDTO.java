package marloncalegao.consultoday_api.dtos.agendamento;

import jakarta.validation.constraints.NotBlank;

public record CancelamentoAgendamentoDTO(
    @NotBlank(message = "O motivo do cancelamento é obrigatório.")
    String motivo
){
    
}
