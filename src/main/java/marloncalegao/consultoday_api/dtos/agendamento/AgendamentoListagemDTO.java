package marloncalegao.consultoday_api.dtos.agendamento;

import java.time.LocalDateTime;

import marloncalegao.consultoday_api.model.Agendamento;

public record AgendamentoListagemDTO (
        Long idAgendamento,
        LocalDateTime dataHora,
        String nomeMedico,
        String CrmMedico,
        String nomePaciente,
        String CpfPaciente,
        LocalDateTime dataCancelamento,
        String MotivoCancelamento
) {
    public AgendamentoListagemDTO(Agendamento agendamento) {
        this(
                agendamento.getId(),
                agendamento.getDataHora(),
                agendamento.getMedico().getNome(),
                agendamento.getMedico().getCrm(),
                agendamento.getPaciente().getNome(),
                agendamento.getPaciente().getCpf(),
                agendamento.getDataCancelamento(),
                agendamento.getMotivoCancelamento()
        );
    }
}
