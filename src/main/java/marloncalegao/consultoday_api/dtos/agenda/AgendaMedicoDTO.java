package marloncalegao.consultoday_api.dtos.agenda;

import java.time.LocalDateTime;
import marloncalegao.consultoday_api.model.AgendaMedico;

public record AgendaMedicoDTO(Long id, LocalDateTime dataHora) {
    public AgendaMedicoDTO(AgendaMedico agenda) {
        this(agenda.getId(), agenda.getDataHora());
    }
}
