package testes;

import domain.Paciente;
import domain.PacienteComparator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.PriorityQueue;
import java.util.Random;

public class PacienteManager {

    public static void main(String[] args) {
        PriorityQueue<Paciente> priorityQueue = new PriorityQueue<>(new PacienteComparator());

        // Criar e enfileirar 16 pacientes (4 em cada faixa etária)
        for (int i = 1; i <= 16; i++) {
            Paciente paciente = criarPacienteAleatorio(i);
            priorityQueue.add(paciente);
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream("src/bdo/fila-de-pacientes.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(priorityQueue);
            objectOutputStream.close();
            System.out.println("Salvo com sucesso");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Método para criar um paciente com dados aleatórios distribuídos por faixa etária
    private static Paciente criarPacienteAleatorio(int id) {
        String nomeCompleto = "Paciente " + id;
        String cpf = String.format("%03d.%03d.%03d-%02d", id, id + 1, id + 2, id + 3);
        char sexo = (id % 2 == 0) ? 'M' : 'F';
        LocalDate dataNascimento = gerarDataNascimentoPorFaixaEtaria(id);
        String relatoQueixasSintomas = "Sintomas aleatórios " + id;
        int prioridade = 1 + id % 5;
        LocalDateTime dataHoraEnfileiramento = LocalDateTime.now().minusMinutes(new Random().nextInt(60));
        String senha = gerarSenha(prioridade, id);

        return new Paciente(nomeCompleto, cpf, sexo, dataNascimento, relatoQueixasSintomas, prioridade, dataHoraEnfileiramento, senha);
    }

    // Método para gerar data de nascimento distribuída por faixa etária
    private static LocalDate gerarDataNascimentoPorFaixaEtaria(int id) {
        LocalDate hoje = LocalDate.now();
        Random random = new Random();

        // Definir intervalo de anos para cada faixa etária
        int faixaEtaria = (id - 1) / 4; // 0-3 -> Criança, 4-7 -> Adolescente, 8-11 -> Adulto, 12-15 -> Idoso
        LocalDate dataNascimento;

        switch (faixaEtaria) {
            case 0: // Criança (0-12 anos)
                dataNascimento = hoje.minusYears(random.nextInt(13)); // 0-12 anos
                break;
            case 1: // Adolescente (13-17 anos)
                dataNascimento = hoje.minusYears(13 + random.nextInt(5)); // 13-17 anos
                break;
            case 2: // Adulto (18-59 anos)
                dataNascimento = hoje.minusYears(18 + random.nextInt(42)); // 18-59 anos
                break;
            case 3: // Idoso (60+ anos)
                dataNascimento = hoje.minusYears(60 + random.nextInt(30)); // 60+ anos
                break;
            default:
                throw new IllegalArgumentException("Faixa etária inválida para id: " + id);
        }

        return dataNascimento;
    }

    // Método para gerar uma senha com base na prioridade e no ID do paciente
    private static String gerarSenha(int prioridade, int id) {
        char prioridadeChar = switch (prioridade) {
            case 5 -> 'R';
            case 4 -> 'O';
            case 3 -> 'Y';
            case 2 -> 'G';
            case 1 -> 'B';
            default -> throw new IllegalArgumentException("Prioridade inválida: " + prioridade);
        };
        return prioridadeChar + String.valueOf(id);
    }
}
