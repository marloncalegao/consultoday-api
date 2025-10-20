package marloncalegao.consultoday_api.dtos.response;

import marloncalegao.consultoday_api.model.Paciente;

public record PacienteResponseDTO (
    Long id,
    String nome,
    String email,
    String cpf,
    String telefone
) {
    public PacienteResponseDTO(Paciente paciente){
        this(
            paciente.getId(),
            paciente.getNome(),
            paciente.getEmail(),
            paciente.getCpf(),
            paciente.getTelefone()
        );
    }
    
}
