package marloncalegao.consultoday_api.dtos.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import marloncalegao.consultoday_api.enums.Especialidade;

public record AgendamentoRequestDTO(
    Long idMedico,

    @NotNull(message = "A data e hora são obrigatórias")
    @Future(message = "A data da consulta deve ser futura")
    LocalDateTime dataHora,
    
    Especialidade especialidade
) {
    
}
