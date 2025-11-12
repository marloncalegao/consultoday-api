package marloncalegao.consultoday_api.dtos.agenda;

import java.time.LocalDateTime;

public record AgendaSlotDTO(
        Long id,
        LocalDateTime dataHora,
        boolean disponivel
) {}
