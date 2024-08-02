package domain;

import java.io.Serializable;
import java.util.Comparator;

public class PacienteComparator implements Comparator<Paciente>, Serializable {
    @Override
    public int compare(Paciente p1, Paciente p2) {
        if (p1.getPrioridade() != p2.getPrioridade()) {
            return p2.getPrioridade() - p1.getPrioridade();
        } else {
            return p1.getDataHoraEnfileiramento().compareTo(p2.getDataHoraEnfileiramento());
        }
    }
}
