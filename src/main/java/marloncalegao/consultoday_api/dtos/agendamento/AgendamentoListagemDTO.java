package marloncalegao.consultoday_api.dtos.agendamento;

import java.time.LocalDateTime;
import marloncalegao.consultoday_api.enums.StatusAgendamento;
import marloncalegao.consultoday_api.model.Agendamento;

public record AgendamentoListagemDTO(
        Long idAgendamento,
        LocalDateTime dataHora,
        String nomeMedico,
        String crmMedico,
        String nomePaciente,
        String cpfPaciente,
        LocalDateTime dataCancelamento,
        String motivoCancelamento,
        StatusAgendamento status
) {
    public AgendamentoListagemDTO(Agendamento a) {
        this(
                a.getId(),
                a.getDataHora(),
                a.getMedico().getNome(),
                a.getMedico().getCrm(),
                a.getPaciente().getNome(),
                a.getPaciente().getCpf(),
                a.getDataCancelamento(),
                a.getMotivoCancelamento(),
                a.getStatus()
        );
    }
}
