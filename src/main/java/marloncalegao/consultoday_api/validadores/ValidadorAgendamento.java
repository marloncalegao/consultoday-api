package marloncalegao.consultoday_api.validadores;

import marloncalegao.consultoday_api.dtos.request.AgendamentoRequestDTO;

public interface ValidadorAgendamento {
    void validar(AgendamentoRequestDTO dados, Long idPaciente);
}
