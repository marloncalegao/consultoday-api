package marloncalegao.consultoday_api.dtos.response;

import marloncalegao.consultoday_api.enums.Especialidade;
import marloncalegao.consultoday_api.model.Medico;

public record MedicoResponseDTO (
    Long id,
    String nome,
    String email,
    String telefone,
    String crm,
    Especialidade especialidade
) {
    public MedicoResponseDTO(Medico medico){
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
