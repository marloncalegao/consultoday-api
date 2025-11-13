package marloncalegao.consultoday_api.dtos.medico;

import marloncalegao.consultoday_api.enums.Especialidade;
import marloncalegao.consultoday_api.model.Medico;

public record MedicoListagemDTO (
    Long id,
    String nome,
    String email,
    String telefone,
    String crm,
    Especialidade especialidade
) {
    public MedicoListagemDTO(Medico medico) {
        this(
            medico.getId(),
            medico.getNome(),
            medico.getEmail(),
            medico.getTelefone(),
            medico.getCrm(),
            medico.getEspecialidade()
        );
    }
    
}
