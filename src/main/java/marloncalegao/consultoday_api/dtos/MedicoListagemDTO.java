package marloncalegao.consultoday_api.dtos;

import marloncalegao.consultoday_api.enums.Especialidade;
import marloncalegao.consultoday_api.model.Medico;

public record MedicoListagemDTO (
    Long id,
    String nome,
    String email,
    String crm,
    Especialidade especialidade
) {
    public MedicoListagemDTO(Medico medico) {
        this(
            medico.getId(),
            medico.getNome(),
            medico.getEmail(),
            medico.getCrm(),
            medico.getEspecialidade()
        );
    }
    
}
