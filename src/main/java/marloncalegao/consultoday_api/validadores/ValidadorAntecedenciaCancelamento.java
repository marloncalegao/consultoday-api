package marloncalegao.consultoday_api.validadores;

import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.LocalDateTime;

import marloncalegao.consultoday_api.exception.ValidacaoException;
import marloncalegao.consultoday_api.model.Agendamento;

@Component
public class ValidadorAntecedenciaCancelamento {
    
    public void validar(Agendamento agendamento) {
        var agora = LocalDateTime.now();
        var dataAgendamento = agendamento.getDataHora();
        var diferencaEmHoras = Duration.between(agora, dataAgendamento).toHours();

        if (diferencaEmHoras < 24) {
            throw new ValidacaoException("O cancelamento deve ser feito com no mínimo 24 horas de antecedência.");
        }
    }
}
