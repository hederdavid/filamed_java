package domain;

import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    private static PriorityQueue<Paciente> fila = new PriorityQueue<>(new PacienteComparator());
    private static Map<String, Integer> qtdPacientesEnfileiradosPorPrioridade;
    private static Scanner scanner = new Scanner(System.in);

    static {
        try {
            fila = carregarPacientesDoBdo();
        } catch (IOException e) {
            System.out.println("Fila vazia no momento!");
        } catch (ClassNotFoundException e) {
            System.out.println("Ocorreu um erro ao tentar carregar os pacientes do banco de dados.");
        }

        qtdPacientesEnfileiradosPorPrioridade = new HashMap<>();
        qtdPacientesEnfileiradosPorPrioridade.put("EMERGENTE", verificarQuantidadeDePacientesPorPrioridade(5));
        qtdPacientesEnfileiradosPorPrioridade.put("MUITO URGENTE", verificarQuantidadeDePacientesPorPrioridade(4));
        qtdPacientesEnfileiradosPorPrioridade.put("URGENTE", verificarQuantidadeDePacientesPorPrioridade(3));
        qtdPacientesEnfileiradosPorPrioridade.put("POUCO URGENTE", verificarQuantidadeDePacientesPorPrioridade(2));
        qtdPacientesEnfileiradosPorPrioridade.put("NÃO URGENTE", verificarQuantidadeDePacientesPorPrioridade(1));

    }

    private static int verificarQuantidadeDePacientesPorPrioridade(int prioridade) {
        int qtd = 0;
        for (Paciente paciente : fila) {
            if (paciente.getPrioridade() == prioridade) {
                qtd++;
            }
        }
        return qtd;
    }

    public static void main(String[] args) {

        int opcaoEscolhida;
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
                case 3 -> verificarIminencia();
                case 4 -> consultarDadosProximoPaciente();
                case 0 -> {
                    try {
                        atualizarBdo();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        } while (opcaoEscolhida != 0);
    }

    private static void adicionarPacienteNaFila() {
        Paciente paciente = cadastrarPaciente();
        fila.add(paciente);
        try {
            atualizarBdo();
        } catch (IOException e) {
            System.out.println("Erro ao salvar o paciente no banco de dados.");
        }
        System.out.println("\n Paciente incluído na fila com sucesso!");
    }

    private static void atenderProximoPaciente() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║           Atender Próximo Paciente         ║");
        System.out.println("╚════════════════════════════════════════════╝");
        int prioridade;
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

    private static void verificarIminencia() {

        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║   ⏳ Verificar Iminência de Atendimento     ║");
        System.out.println("╚════════════════════════════════════════════╝");
        String cpf;
        while (true) {
            System.out.print("🆔  Digite o CPF do paciente: ");
            cpf = scanner.nextLine().replace(".", "").replace("-", "");
            if (cpf.matches("\\d{11}")) {
                Paciente proximoPaciente = fila.peek();
                if (proximoPaciente != null && proximoPaciente.getCpf().equals(cpf)) {
                    System.out.println("\nO paciente " + proximoPaciente.getNomeCompleto() + " de cpf " + proximoPaciente.getCpf() + " é o próximo a ser atendido.");
                } else {
                    System.out.println("\nO paciente de cpf " + cpf + " não é o próximo a ser atendido.");
                }
                break;

            } else {
                System.out.println("❌ CPF inválido! Digite um CPF com 11 dígitos.");
            }
        }
    }


    private static void consultarDadosProximoPaciente() {
        Paciente proximoPaciente = fila.peek();
        if (proximoPaciente != null) {
            System.out.println("Nome: " + proximoPaciente.getNomeCompleto());
            System.out.println("Senha: " + proximoPaciente.getSenha());
            System.out.println("Tempo de espera atual: " + calcularTempoDePermanencia(proximoPaciente.getDataHoraEnfileiramento()));
        } else {
            System.out.println("Fila Vazia!");
        }
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

        LocalDate dataNascimento;

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

        int prioridade;
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
            case 5 -> senha = "R-" + qtdPacientesEnfileiradosPorPrioridade.get("EMERGENTE").toString();
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
        switch (prioridade) {
            case 5, 4, 3, 2, 1 -> {
                boolean encontrado = false;
                for (Paciente paciente : fila) {
                    if (paciente.getPrioridade() == prioridade) {
                        System.out.println("\n╔════════════════════════════════════════════╗");
                        System.out.println("║           🏥 Paciente Atendido             ║");
                        System.out.println("╚════════════════════════════════════════════╝");
                        System.out.println("Nome: " + paciente.getNomeCompleto());
                        System.out.println("Senha: " + paciente.getSenha());
                        System.out.println("Tempo de espera: " + calcularTempoDePermanencia(paciente.getDataHoraEnfileiramento()));

                        fila.remove(paciente);
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado) {
                    System.out.println("Nenhum paciente encontrado com prioridade " + prioridade);
                }
            }

            case 0 -> {
                if (!fila.isEmpty()) {
                    Paciente paciente = fila.poll();
                    System.out.println("╔════════════════════════════════════════════╗");
                    System.out.println("║           🏥 Paciente Atendido             ║");
                    System.out.println("╚════════════════════════════════════════════╝");
                    System.out.println("Nome: " + paciente.getNomeCompleto());
                    System.out.println("Senha: " + paciente.getSenha());
                    System.out.println("Tempo de espera: " + calcularTempoDePermanencia(paciente.getDataHoraEnfileiramento()));
                } else {
                    System.out.println("Fila Vazia!");
                }
            }

            default -> System.out.println("Prioridade inválida!");
        }

        try {
            atualizarBdo();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void atualizarBdo() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("src/bdo/fila-de-pacientes.txt");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(fila);
    }

    private static PriorityQueue<Paciente> carregarPacientesDoBdo() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("src/bdo/fila-de-pacientes.txt");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        fila = (PriorityQueue<Paciente>) objectInputStream.readObject();
        return fila;
    }

    private static String calcularTempoDePermanencia(LocalDateTime dataHoraEnfileiramento) {
        LocalDateTime agora = LocalDateTime.now();

        Duration duracao = Duration.between(dataHoraEnfileiramento, agora);

        long minutos = duracao.toMinutes();
        long segundos = duracao.getSeconds() % 60;

        return String.format("%d minutos e %d segundos", minutos, segundos);
    }
}