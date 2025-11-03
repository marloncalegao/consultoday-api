package marloncalegao.consultoday_api.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import marloncalegao.consultoday_api.enums.StatusAgendamento;

@Table(name = "agendamentos")
@Entity(name = "Agendamento")
public class Agendamento {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "medico_id")
    private Medico medico;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    private LocalDateTime dataHora;
    private LocalDateTime dataCancelamento;

    private String motivoCancelamento;

    @Enumerated(EnumType.STRING)
    private StatusAgendamento status;
    
    //-- Getters e Setters --

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Medico getMedico() {
        return this.medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Paciente getPaciente() {
        return this.paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public LocalDateTime getDataHora() {
        return this.dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public LocalDateTime getDataCancelamento() {
        return this.dataCancelamento;
    }

    public void setDataCancelamento(LocalDateTime dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public String getMotivoCancelamento() {
        return this.motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public StatusAgendamento getStatus() {
        return this.status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }

    //-- Construtores --

    public Agendamento(Medico medico, Paciente paciente, LocalDateTime dataHora, StatusAgendamento status) {
        this.medico = medico;
        this.paciente = paciente;
        this.dataHora = dataHora;
        this.status = status;
    }

    public Agendamento() {
    }

}
