package marloncalegao.consultoday_api.validadores;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import marloncalegao.consultoday_api.dtos.request.AgendamentoRequestDTO;
import marloncalegao.consultoday_api.exception.ValidacaoException;

public class ValidadorAntecedencia implements ValidadorAgendamento {
    
    public void validar(AgendamentoRequestDTO dados, Long idPaciente) {
        var agora = LocalDateTime.now();
        var dataAgendamento = dados.dataHora();

        var diferencaEmMinutos = ChronoUnit.MINUTES.between(agora, dataAgendamento);

        if (diferencaEmMinutos < 30) {
            throw new ValidacaoException("A consulta deve ser agendada com no mínimo 30 minutos de antecedência.");
        }
    }
}
