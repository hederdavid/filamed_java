import java.time.LocalDate;
import java.time.LocalDateTime;

public class Paciente {
    private String nomeCompleto;
    private String cpf;
    private char sexo;
    private LocalDate dataNascimento;
    private String relatoQueixasSintomas;
    private int prioridade;
    private LocalDateTime dataHoraEnfileiramento;
    private LocalDateTime dataHoraDesenfileiramento = null;
    private String senha = "";

    public Paciente(String nomeCompleto, String cpf, char sexo, LocalDate dataNascimento,
                    String relatoQueixasSintomas, int prioridade, LocalDateTime dataHoraEnfileiramento, String senha) {
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.sexo = sexo;
        this.dataNascimento = dataNascimento;
        this.relatoQueixasSintomas = relatoQueixasSintomas;
        this.prioridade = prioridade;
        this.dataHoraEnfileiramento = dataHoraEnfileiramento;
        this.senha = senha;
    }

    // Getters and Setters
    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public char getSexo() {
        return sexo;
    }

    public void setSexo(char sexo) {
        this.sexo = sexo;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getRelatoQueixasSintomas() {
        return relatoQueixasSintomas;
    }

    public void setRelatoQueixasSintomas(String relatoQueixasSintomas) {
        this.relatoQueixasSintomas = relatoQueixasSintomas;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public LocalDateTime getDataHoraEnfileiramento() {
        return dataHoraEnfileiramento;
    }

    public void setDataHoraEnfileiramento(LocalDateTime dataHoraEnfileiramento) {
        this.dataHoraEnfileiramento = dataHoraEnfileiramento;
    }

    @Override
    public String toString() {
        return "Paciente{" +
                "nomeCompleto='" + nomeCompleto + '\'' +
                ", cpf='" + cpf + '\'' +
                ", sexo=" + sexo +
                ", dataNascimento=" + dataNascimento +
                ", relatoQueixasSintomas='" + relatoQueixasSintomas + '\'' +
                ", prioridade=" + prioridade +
                ", dataHoraEnfileiramento=" + dataHoraEnfileiramento +
                ", dataHoraDesenfileiramento=" + dataHoraDesenfileiramento +
                ", senha='" + senha + '\'' +
                '}';
    }
}