import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    public static PriorityQueue<Paciente> fila = new PriorityQueue<>(new PacienteComparator());
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
    public static void main(String[] args) throws InterruptedException {
        fila.add(new Paciente("João Silva", "12345678900", 'M', LocalDate.of(1980, 5, 15),
                "Dor de cabeça intensa", 4, LocalDateTime.now(), "senha123"));
        Thread.sleep(1000);

        fila.add(new Paciente("Maria Santos", "98765432100", 'F', LocalDate.of(1990, 3, 20),
                "Febre alta", 5, LocalDateTime.now(), "senha456"));
        Thread.sleep(1000);

        fila.add(new Paciente("Carlos Lima", "45612378900", 'M', LocalDate.of(1975, 7, 10),
                "Tontura", 3, LocalDateTime.now(), "senha789"));
        Thread.sleep(1000);

        fila.add(new Paciente("Ana Costa", "78945612300", 'F', LocalDate.of(2000, 1, 5),
                "Dificuldade para respirar", 5, LocalDateTime.now(), "senha012"));
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
            System.out.println("║  2.  Atender próximo paciente                                 ║");
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
                case 1 -> adicionarPacienteNaFila();
                case 2 -> atenderProximoPaciente();
            }

        } while (opcaoEscolhida != 0);
    }

    private static void adicionarPacienteNaFila() {
        Paciente paciente = cadastrarPaciente();
        fila.add(paciente);
        System.out.println(fila);
    }

    private static void atenderProximoPaciente() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║           Atender Próximo Paciente         ║");
        System.out.println("╚════════════════════════════════════════════╝");
        int prioridade = 0;
        while (true) {
            System.out.print("⚠️ Digite '0' para selecionar automaticamente ou \nDigite a classificação de risco (5 - Emergência, 4 - Muito Urgente, 3 - Urgente, 2 - Pouco Urgente, 1 - Não Urgente): ");
            prioridade = scanner.nextInt();
            if (prioridade >= 0 && prioridade <= 5) {
                break;
            } else {
                System.out.println("❌ Prioridade inválida! Digite um número entre 1 e 5.");
            }
        }

        atenderPacientePelaPrioridade(prioridade);
    }

    private static Paciente cadastrarPaciente() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║             Cadastrar Paciente             ║");
        System.out.println("╚════════════════════════════════════════════╝");

        System.out.print("👤  Digite o nome completo do paciente: ");
        String nomeCompleto = scanner.nextLine().toUpperCase();

        String cpf;
        while (true) {
            System.out.print("🆔  Digite o CPF do paciente: ");
            cpf = scanner.nextLine().replace(".", "").replace("-", "");
            if (cpf.matches("\\d{11}")) {
                if (!cpfJaExiste(cpf)) {
                    break;
                } else {
                    System.out.println("❌ CPF já cadastrado! Digite um CPF diferente.");
                }
            } else {
                System.out.println("❌ CPF inválido! Digite um CPF com 11 dígitos.");
            }
        }

        char sexo;
        while (true) {
            System.out.print("⚧️  Digite o sexo do paciente (Masculino/Feminino/Outros): ");
            sexo = scanner.nextLine().toUpperCase().charAt(0);
            if (sexo == 'M' || sexo == 'F' || sexo == 'O') {
                break;
            } else {
                System.out.println("❌ Sexo inválido! Digite M, F ou O.");
            }
        }

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
                if (prioridade >= 1 && prioridade <= 5) {
                    break;
                } else {
                    System.out.println("❌ Prioridade inválida! Digite um número entre 1 e 5.");
                }
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

    private static boolean cpfJaExiste(String cpf) {
        return fila.stream().anyMatch(paciente -> paciente.getCpf().equals(cpf));
    }

    private static void atenderPacientePelaPrioridade(int prioridade) {
        switch(prioridade) {
            case 5 -> {
                for (Paciente paciente : fila) {
                    if (paciente.getPrioridade() == 5) {
                        System.out.println(fila.remove(paciente));
                        break;
                    }
                }
            }
            case 4 -> {
                for (Paciente paciente : fila) {
                    if (paciente.getPrioridade() == 4) {
                        System.out.println(fila.remove(paciente));
                        break;
                    }
                }

            }

            case 3 -> {
                for (Paciente paciente : fila) {
                    if (paciente.getPrioridade() == 3) {
                        System.out.println(fila.remove(paciente));
                        break;
                    }
                }
            }

            case 2 -> {
                for (Paciente paciente : fila) {
                    if (paciente.getPrioridade() == 2) {
                        System.out.println(fila.remove(paciente));
                        break;
                    }
                }
            }
            case 1 -> {
                for (Paciente paciente : fila) {
                    if (paciente.getPrioridade() == 1) {
                        System.out.println(fila.remove(paciente));
                        break;
                    }
                }
            }
            case 0 -> {
                if (!fila.isEmpty()) {
                    System.out.println(fila.poll());
                } else {
                    System.out.println("Fila Vazia!");
                }

            }

            default -> System.out.println("Prioridade inválida!");
        }
    }
}