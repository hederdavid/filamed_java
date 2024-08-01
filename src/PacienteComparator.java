import java.util.Comparator;

class PacienteComparator implements Comparator<Paciente> {
    @Override
    public int compare(Paciente p1, Paciente p2) {
        if (p1.getPrioridade() != p2.getPrioridade()) {
            // Ordem decrescente de prioridade
            return p2.getPrioridade() - p1.getPrioridade();
        } else {
            // Se as prioridades são iguais, compara pela data e hora de enfileiramento
            return p1.getDataHoraEnfileiramento().compareTo(p2.getDataHoraEnfileiramento());
        }
    }
}
