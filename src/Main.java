import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    public static Queue<Paciente> fila = new ArrayDeque<>();
    public static Map<String, Integer> qtdPacientesEnfileiradosPorPrioridade;
    public static final Scanner scanner = new Scanner(System.in);

    static {
        qtdPacientesEnfileiradosPorPrioridade = new HashMap<>();
        qtdPacientesEnfileiradosPorPrioridade.put("EMERGENTE", 0);
        qtdPacientesEnfileiradosPorPrioridade.put("MUITO URGENTE", 0);
        qtdPacientesEnfileiradosPorPrioridade.put("URGENTE", 0);
        qtdPacientesEnfileiradosPorPrioridade.put("POUCO URGENTE", 0);
        qtdPacientesEnfileiradosPorPrioridade.put("NÃO URGENTE", 0);
    }
    public static void main(String[] args) {
        int opcaoEscolhida = -1;
        do {
            System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
            System.out.println("║                                                               ║");
            System.out.println("║         FILAMED - GERENCIADOR DE FILAS DE PRIORIDADE          ║");
            System.out.println("║                                                               ║");
            System.out.println("╠═══════════════════════════════════════════════════════════════╣");
            System.out.println("║                                                               ║");
            System.out.println("║                       Selecione uma opção:                    ║");
            System.out.println("║                                                               ║");
            System.out.println("║  1.  Adicionar paciente                                       ║");
            System.out.println("║  2.  Chamar próximo paciente                                  ║");
            System.out.println("║  3.  Verificar iminência de atendimento                       ║");
            System.out.println("║  4.  Consultar próximo paciente de uma fila                   ║");
            System.out.println("║  5.  Consultar estatísticas                                   ║");
            System.out.println("║  0.  Sair                                                     ║");
            System.out.println("║                                                               ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════╝");

            while (true) {
                try {
                    System.out.print("🅾️Opção: ");
                    opcaoEscolhida = scanner.nextInt();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("❌ Insira uma opção válida!");
                    scanner.nextLine();
                }
            }

            scanner.nextLine();

            switch (opcaoEscolhida) {
                case 1-> adicionarPacienteNaFila();
            }

        } while (opcaoEscolhida != 0);
    }

    private static void adicionarPacienteNaFila() {
        Paciente paciente = cadastrarPaciente();
        fila.add(paciente);
        System.out.println(fila);
    }

    private static Paciente cadastrarPaciente() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║             Cadastrar Paciente             ║");
        System.out.println("╚════════════════════════════════════════════╝");

        System.out.print("👤  Digite o nome completo do paciente: ");
        String nomeCompleto = scanner.nextLine().toUpperCase();

        System.out.print("🆔  Digite o CPF do paciente: ");
        String cpf = scanner.nextLine();

        System.out.print("⚧️  Digite o sexo do paciente (Masculino/Feminino/Outros): ");
        char sexo = scanner.nextLine().toUpperCase().charAt(0);

        LocalDate dataNascimento = null;

        while (true) {
            System.out.print("📅  Digite a data de nascimento do paciente (dd/MM/yyyy): ");
            String dataNascimentoStr = scanner.nextLine();
            try {
                dataNascimento = LocalDate.parse(dataNascimentoStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                break;
            } catch (DateTimeParseException e) {
                System.out.println("❌ Digite a data no formato correto! Ex: 05/11/2003");
            }
        }

        System.out.print("🤕  Relato de queixas e sintomas: ");
        String relatoQueixaSintomas = scanner.nextLine();

        int prioridade = 0;
        while (true) {
            try {
                System.out.print("🚨  Digite a prioridade do paciente (1 - NÃO URGENTE, 2 - POUCO URGENTE, 3 - URGENTE, " +
                        "4 - MUITO URGENTE, 5 - EMERGENTE): ");
                prioridade = scanner.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("❌ Prioridade inválida! Tente novamente.");
                scanner.nextLine();
            }
        }

        LocalDateTime dataHoraEnfileiramento = LocalDateTime.now();
        atualizarQtdPacientesEnfileiradosPorPrioridade(prioridade);
        String senha = definirSenhaPaciente(prioridade);

        return new Paciente(nomeCompleto, cpf, sexo, dataNascimento, relatoQueixaSintomas, prioridade,
                dataHoraEnfileiramento, senha);
    }

    private static String definirSenhaPaciente(int prioridade) {
        String senha = null;
        switch (prioridade) {
            case 5 ->  senha = "R-" + qtdPacientesEnfileiradosPorPrioridade.get("EMERGENTE").toString();
            case 4 -> senha = "O-" + qtdPacientesEnfileiradosPorPrioridade.get("MUITO URGENTE").toString();
            case 3 -> senha = "Y-" + qtdPacientesEnfileiradosPorPrioridade.get("URGENTE").toString();
            case 2 -> senha = "G-" + qtdPacientesEnfileiradosPorPrioridade.get("POUCO URGENTE").toString();
            case 1 -> senha = "B-" + qtdPacientesEnfileiradosPorPrioridade.get("NÃO URGENTE").toString();
            default -> System.out.println("Algo deu errado ao tentar definir a senha do paciente");
        }
        return senha;
    }

    private static void atualizarQtdPacientesEnfileiradosPorPrioridade(int prioridade) {
        switch (prioridade) {
            case 5 -> {
                int quantidadeAtual = qtdPacientesEnfileiradosPorPrioridade.get("EMERGENTE");
                qtdPacientesEnfileiradosPorPrioridade.put("EMERGENTE", quantidadeAtual + 1);
            }

            case 4 -> {
                int quantidadeAtual = qtdPacientesEnfileiradosPorPrioridade.get("MUITO URGENTE");
                qtdPacientesEnfileiradosPorPrioridade.put("MUITO URGENTE", quantidadeAtual + 1);
            }

            case 3 -> {
                int quantidadeAtualizada = qtdPacientesEnfileiradosPorPrioridade.get("URGENTE");
                qtdPacientesEnfileiradosPorPrioridade.put("URGENTE", quantidadeAtualizada + 1);
            }

            case 2 -> {
                int quantidadeAtualizada = qtdPacientesEnfileiradosPorPrioridade.get("POUCO URGENTE");
                qtdPacientesEnfileiradosPorPrioridade.put("POUCO URGENTE", quantidadeAtualizada + 1);

            }

            case 1 -> {
                int quantidadeAtualizada = qtdPacientesEnfileiradosPorPrioridade.get("NÃO URGENTE");
                qtdPacientesEnfileiradosPorPrioridade.put("NÃO URGENTE", quantidadeAtualizada + 1);
            }

            default -> System.out.println("Algo deu errado ao atualizar a quantidade de pacientes");
        }
    }
}