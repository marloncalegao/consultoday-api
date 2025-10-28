package marloncalegao.consultoday_api.dtos.response;

import java.time.LocalDateTime;

import marloncalegao.consultoday_api.model.Agendamento;

public record AgendamentoResponseDTO(
    Long id,
    Long idMedico,
    Long idPaciente,
    LocalDateTime dataHora
) { public AgendamentoResponseDTO(Agendamento agendamento) {
        this(
            agendamento.getId(),
            agendamento.getMedico().getId(),
            agendamento.getPaciente().getId(),
            agendamento.getDataHora()
        );
    }
}
