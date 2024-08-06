package domain;

import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    private static PriorityQueue<Paciente> fila = new PriorityQueue<>(new PacienteComparator());
    private static List<Paciente> pacientesAtendidos = new ArrayList<>();
    private static final Map<String, Integer> qtdAtualPacientesEnfileiradosPorPrioridade;
    private static final Map<String, Integer> qtdDiariaPacientesEnfileiradosPorPrioridade;
    private static final Map<String, Integer> qtdPacientesPorFaixaEtaria;
    private static final Scanner scanner = new Scanner(System.in);

    static {
        // Inicialização de algumas variáveis
        try {
            fila = (PriorityQueue<Paciente>) carregarPacientesDoBdo(fila);
            pacientesAtendidos = (List<Paciente>) carregarPacientesDoBdo(pacientesAtendidos);
        } catch (IOException e) {
            System.out.println("Fila atual ou lista de pacientes atendidos vazia no momento!");
        } catch (ClassNotFoundException e) {
            System.out.println("Ocorreu um erro ao tentar carregar os pacientes do banco de dados.");
        }

        qtdPacientesPorFaixaEtaria = new HashMap<>();
        qtdPacientesPorFaixaEtaria.put("CRIANÇAS", 0);
        qtdPacientesPorFaixaEtaria.put("ADOLESCENTES", 0);
        qtdPacientesPorFaixaEtaria.put("ADULTOS", 0);
        qtdPacientesPorFaixaEtaria.put("IDOSOS", 0);

        //Atualiza o hashmap acima
        verificarQuantidadeDePacientesPorFaixaEtaria();

        qtdAtualPacientesEnfileiradosPorPrioridade = new HashMap<>();
        qtdAtualPacientesEnfileiradosPorPrioridade.put("EMERGENTE", verificarQuantidadeDePacientesPorPrioridade(5,
                fila));
        qtdAtualPacientesEnfileiradosPorPrioridade.put("MUITO URGENTE", verificarQuantidadeDePacientesPorPrioridade(4,
                fila));
        qtdAtualPacientesEnfileiradosPorPrioridade.put("URGENTE", verificarQuantidadeDePacientesPorPrioridade(3,
                fila));
        qtdAtualPacientesEnfileiradosPorPrioridade.put("POUCO URGENTE", verificarQuantidadeDePacientesPorPrioridade(2,
                fila));
        qtdAtualPacientesEnfileiradosPorPrioridade.put("NÃO URGENTE", verificarQuantidadeDePacientesPorPrioridade(1,
                fila));

        qtdDiariaPacientesEnfileiradosPorPrioridade = new HashMap<>();
        qtdDiariaPacientesEnfileiradosPorPrioridade.put("EMERGENTE", verificarQuantidadeDePacientesPorPrioridade(5,
                pacientesAtendidos));
        qtdDiariaPacientesEnfileiradosPorPrioridade.put("MUITO URGENTE", verificarQuantidadeDePacientesPorPrioridade(4,
                pacientesAtendidos));
        qtdDiariaPacientesEnfileiradosPorPrioridade.put("URGENTE", verificarQuantidadeDePacientesPorPrioridade(3,
                pacientesAtendidos));
        qtdDiariaPacientesEnfileiradosPorPrioridade.put("POUCO URGENTE", verificarQuantidadeDePacientesPorPrioridade(2,
                pacientesAtendidos));
        qtdDiariaPacientesEnfileiradosPorPrioridade.put("NÃO URGENTE", verificarQuantidadeDePacientesPorPrioridade(1,
                pacientesAtendidos));
    }

    private static void verificarQuantidadeDePacientesPorFaixaEtaria() {
        for (Paciente paciente : fila) {
            int idade = calcularIdade(paciente.getDataNascimento());
            if (idade >= 0 && idade <= 11) {
                int qtdAtual = qtdPacientesPorFaixaEtaria.get("CRIANÇAS");
                qtdPacientesPorFaixaEtaria.put("CRIANÇAS", qtdAtual + 1);
            } else if (idade >= 12 && idade <= 17) {
                int qtdAtual = qtdPacientesPorFaixaEtaria.get("ADOLESCENTES");
                qtdPacientesPorFaixaEtaria.put("ADOLESCENTES", qtdAtual + 1);
            } else if (idade >= 18 && idade <= 59) {
                int qtdAtual = qtdPacientesPorFaixaEtaria.get("ADULTOS");
                qtdPacientesPorFaixaEtaria.put("ADULTOS", qtdAtual + 1);
            } else if (idade >= 60) {
                int qtdAtual = qtdPacientesPorFaixaEtaria.get("IDOSOS");
                qtdPacientesPorFaixaEtaria.put("IDOSOS", qtdAtual + 1);
            }
        }
    }

    private static int verificarQuantidadeDePacientesPorPrioridade(int prioridade, Collection<Paciente> colecao) {
        int qtd = 0;
        for (Paciente paciente : colecao) {
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
            System.out.println("║  4.  Consultar dados próximo paciente                         ║");
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
                case 5 -> consultarEstatisticas();
                case 0 -> {
                    try {
                        atualizarBdo(fila);
                        atualizarBdo(pacientesAtendidos);
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
        atualizarQtdPacientesPorFaixaEtaria(1, paciente.getDataNascimento());
        try {
            atualizarBdo(fila);
            System.out.println("\n ✅ Paciente incluído na fila com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao salvar o paciente no banco de dados.");
        }
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
            System.out.println("❌ Fila Vazia!");
        }
    }

    private static void consultarEstatisticas() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║                  CONSULTAS DE CARÁTER ESTATÍSTICO             ║");
        System.out.println("║                                                               ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                               ║");
        System.out.println("║  1. Quantidade de pacientes no momento por fila               ║");
        System.out.println("║  2. Quantidade total de pacientes por faixa etária            ║");
        System.out.println("║     (crianças, adolescentes, adultos e idosos)                ║");
        System.out.println("║  3. Tempo médio de permanência em fila de atendimento         ║");
        System.out.println("║  4. Percentual de pacientes em tempo inferior ao recomendado  ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.print("Opção escolhida: ");
        int opcaoEscolhida = scanner.nextInt();

        switch (opcaoEscolhida) {
            case 1 -> quantidadeDePacientesAtual();
            case 2 -> quantidadeDePacientesPorFaixaEtaria();
            case 3 -> tempoMedioDePermanencia();
            case 4 -> percentualPacientesTempoMedioRecomendado();
            default -> System.out.println("Opção inválida!");
        }

    }

    private static void quantidadeDePacientesAtual() {
        int qtdEmergente = 0, qtdMuitoUrgente = 0, qtdUrgente = 0, qtdPoucoUrgente = 0, qtdNaoUrgente = 0;

        for (Paciente paciente : fila) {
            switch (paciente.getPrioridade()) {
                case 5 -> qtdEmergente++;
                case 4 -> qtdMuitoUrgente++;
                case 3 -> qtdUrgente++;
                case 2 -> qtdPoucoUrgente++;
                case 1 -> qtdNaoUrgente++;
                default -> System.out.println("Erro ao calcular qtd de pacientes na fila.");
            }
        }

        System.out.println("----------------------------------------");
        System.out.println("Total de pacientes na fila: " + (qtdEmergente + qtdMuitoUrgente + qtdUrgente + qtdPoucoUrgente
                + qtdNaoUrgente));
        System.out.println("\uD83D\uDFE5Emergente: " + qtdEmergente);
        System.out.println("\uD83D\uDFE7Muito urgente: " + qtdMuitoUrgente);
        System.out.println("\uD83D\uDFE8Urgente: " + qtdUrgente);
        System.out.println("\uD83D\uDFE9Pouco urgente: " + qtdPoucoUrgente);
        System.out.println("\uD83D\uDFE6Não urgente: " + qtdNaoUrgente);
        System.out.println("----------------------------------------");
    }

    private static void tempoMedioDePermanencia() {
        if (pacientesAtendidos.isEmpty()) {
            System.out.println("❌ Nenhum paciente atendido hoje.");
            return;
        }

        long totalEmergente = 0, totalMuitoUrgente = 0, totalUrgente = 0, totalPoucoUrgente = 0, totalNaoUrgente = 0;
        int contadorEmergente = 0, contadorMuitoUrgente = 0, contadorUrgente = 0, contadorPoucoUrgente = 0, contadorNaoUrgente = 0;

        for (Paciente paciente : pacientesAtendidos) {
            Duration duracao = Duration.between(paciente.getDataHoraEnfileiramento(), paciente.getDataHoraDesenfileiramento());
            long segundos = duracao.getSeconds();

            switch (paciente.getPrioridade()) {
                case 5 -> {
                    totalEmergente += segundos;
                    contadorEmergente++;
                }
                case 4 -> {
                    totalMuitoUrgente += segundos;
                    contadorMuitoUrgente++;
                }
                case 3 -> {
                    totalUrgente += segundos;
                    contadorUrgente++;
                }
                case 2 -> {
                    totalPoucoUrgente += segundos;
                    contadorPoucoUrgente++;
                }
                case 1 -> {
                    totalNaoUrgente += segundos;
                    contadorNaoUrgente++;
                }
                default -> System.out.println("Algo deu errado ao tentar calcular a média de tempo de permanência.");
            }
        }

        printMedia("Emergente", totalEmergente, contadorEmergente);
        printMedia("Muito Urgente", totalMuitoUrgente, contadorMuitoUrgente);
        printMedia("Urgente", totalUrgente, contadorUrgente);
        printMedia("Pouco Urgente", totalPoucoUrgente, contadorPoucoUrgente);
        printMedia("Não Urgente", totalNaoUrgente, contadorNaoUrgente);
    }

    private static void printMedia(String prioridade, long totalSegundos, int contador) {
        if (contador == 0) {
            System.out.printf("Média %s: 0 minutos e 0 segundos.%n", prioridade);
        } else {
            long mediaSegundos = totalSegundos / contador;
            long minutos = mediaSegundos / 60;
            long segundos = mediaSegundos % 60;
            System.out.printf("Média %s: %d minutos e %d segundos.%n", prioridade, minutos, segundos);
        }
    }

    private static void percentualPacientesTempoMedioRecomendado() {
        if (pacientesAtendidos.isEmpty()) {
            System.out.println("❌ Nenhum paciente atendido hoje.");
            return;
        }

        int pacientesEmergenteDentroDoTempo = 0;
        int pacientesMuitoUrgenteDentroDoTempo = 0;
        int pacientesUrgenteDentroDoTempo = 0;
        int pacientesPoucoUrgenteDentroDoTempo = 0;
        int pacientesNaoUrgenteDentroDoTempo = 0;

        int totalEmergente = 0;
        int totalMuitoUrgente = 0;
        int totalUrgente = 0;
        int totalPoucoUrgente = 0;
        int totalNaoUrgente = 0;

        for (Paciente paciente : pacientesAtendidos) {
            Duration duracao = Duration.between(paciente.getDataHoraEnfileiramento(), paciente.getDataHoraDesenfileiramento());
            long minutos = duracao.toMinutes();

            switch (paciente.getPrioridade()) {
                case 5 -> {
                    totalEmergente++;
                    if (minutos <= 4) {
                        pacientesEmergenteDentroDoTempo++;
                    }
                }
                case 4 -> {
                    totalMuitoUrgente++;
                    if (minutos <= 10) {
                        pacientesMuitoUrgenteDentroDoTempo++;
                    }
                }
                case 3 -> {
                    totalUrgente++;
                    if (minutos <= 50) {
                        pacientesUrgenteDentroDoTempo++;
                    }
                }
                case 2 -> {
                    totalPoucoUrgente++;
                    if (minutos <= 120) {
                        pacientesPoucoUrgenteDentroDoTempo++;
                    }
                }
                case 1 -> {
                    totalNaoUrgente++;
                    if (minutos <= 240) {
                        pacientesNaoUrgenteDentroDoTempo++;
                    }
                }
            }
        }

        System.out.printf("Percentual de pacientes Emergente atendidos no tempo recomendado: %.2f%%%n",
                totalEmergente == 0 ? 0 : (pacientesEmergenteDentroDoTempo * 100.0 / totalEmergente));
        System.out.printf("Percentual de pacientes Muito Urgente atendidos no tempo recomendado: %.2f%%%n",
                totalMuitoUrgente == 0 ? 0 : (pacientesMuitoUrgenteDentroDoTempo * 100.0 / totalMuitoUrgente));
        System.out.printf("Percentual de pacientes Urgente atendidos no tempo recomendado: %.2f%%%n",
                totalUrgente == 0 ? 0 : (pacientesUrgenteDentroDoTempo * 100.0 / totalUrgente));
        System.out.printf("Percentual de pacientes Pouco Urgente atendidos no tempo recomendado: %.2f%%%n",
                totalPoucoUrgente == 0 ? 0 : (pacientesPoucoUrgenteDentroDoTempo * 100.0 / totalPoucoUrgente));
        System.out.printf("Percentual de pacientes Não Urgente atendidos no tempo recomendado: %.2f%%%n",
                totalNaoUrgente == 0 ? 0 : (pacientesNaoUrgenteDentroDoTempo * 100.0 / totalNaoUrgente));
    }


    private static void quantidadeDePacientesPorFaixaEtaria() {
        System.out.println("----------------------------------------");
        System.out.println("Crianças: " + qtdPacientesPorFaixaEtaria.get("CRIANÇAS"));
        System.out.println("Adolescentes: " + qtdPacientesPorFaixaEtaria.get("ADOLESCENTES"));
        System.out.println("Adultos: " + qtdPacientesPorFaixaEtaria.get("ADULTOS"));
        System.out.println("Idosos: " + qtdPacientesPorFaixaEtaria.get("IDOSOS"));
        System.out.println("----------------------------------------");
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
            case 5 -> senha = "R-" + qtdDiariaPacientesEnfileiradosPorPrioridade.get("EMERGENTE").toString();
            case 4 -> senha = "O-" + qtdDiariaPacientesEnfileiradosPorPrioridade.get("MUITO URGENTE").toString();
            case 3 -> senha = "Y-" + qtdDiariaPacientesEnfileiradosPorPrioridade.get("URGENTE").toString();
            case 2 -> senha = "G-" + qtdDiariaPacientesEnfileiradosPorPrioridade.get("POUCO URGENTE").toString();
            case 1 -> senha = "B-" + qtdDiariaPacientesEnfileiradosPorPrioridade.get("NÃO URGENTE").toString();
            default -> System.out.println("❌ Algo deu errado ao tentar definir a senha do paciente");
        }
        return senha;
    }

    private static void atualizarQtdPacientesEnfileiradosPorPrioridade(int prioridade) {
        switch (prioridade) {
            case 5 -> {
                int quantidadeAtual = qtdAtualPacientesEnfileiradosPorPrioridade.get("EMERGENTE");
                qtdAtualPacientesEnfileiradosPorPrioridade.put("EMERGENTE", quantidadeAtual + 1);
                quantidadeAtual = qtdDiariaPacientesEnfileiradosPorPrioridade.get("EMERGENTE");
                qtdDiariaPacientesEnfileiradosPorPrioridade.put("EMERGENTE", quantidadeAtual + 1);
            }

            case 4 -> {
                int quantidadeAtual = qtdAtualPacientesEnfileiradosPorPrioridade.get("MUITO URGENTE");
                qtdAtualPacientesEnfileiradosPorPrioridade.put("MUITO URGENTE", quantidadeAtual + 1);
                quantidadeAtual = qtdDiariaPacientesEnfileiradosPorPrioridade.get("EMERGENTE");
                qtdDiariaPacientesEnfileiradosPorPrioridade.put("EMERGENTE", quantidadeAtual + 1);
            }

            case 3 -> {
                int quantidadeAtual = qtdAtualPacientesEnfileiradosPorPrioridade.get("URGENTE");
                qtdAtualPacientesEnfileiradosPorPrioridade.put("URGENTE", quantidadeAtual + 1);
                quantidadeAtual = qtdDiariaPacientesEnfileiradosPorPrioridade.get("EMERGENTE");
                qtdDiariaPacientesEnfileiradosPorPrioridade.put("EMERGENTE", quantidadeAtual + 1);
            }

            case 2 -> {
                int quantidadeAtual = qtdAtualPacientesEnfileiradosPorPrioridade.get("POUCO URGENTE");
                qtdAtualPacientesEnfileiradosPorPrioridade.put("POUCO URGENTE", quantidadeAtual + 1);
                quantidadeAtual = qtdDiariaPacientesEnfileiradosPorPrioridade.get("EMERGENTE");
                qtdDiariaPacientesEnfileiradosPorPrioridade.put("EMERGENTE", quantidadeAtual + 1);

            }

            case 1 -> {
                int quantidadeAtual = qtdAtualPacientesEnfileiradosPorPrioridade.get("NÃO URGENTE");
                qtdAtualPacientesEnfileiradosPorPrioridade.put("NÃO URGENTE", quantidadeAtual + 1);
                quantidadeAtual = qtdDiariaPacientesEnfileiradosPorPrioridade.get("EMERGENTE");
                qtdDiariaPacientesEnfileiradosPorPrioridade.put("EMERGENTE", quantidadeAtual + 1);
            }

            default -> System.out.println("❌ Algo deu errado ao atualizar a quantidade de pacientes");
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

                        paciente.setDataHoraDesenfileiramento(LocalDateTime.now());
                        pacientesAtendidos.add(paciente);
                        try {
                            atualizarBdo(pacientesAtendidos);
                        } catch (IOException e) {
                            System.out.println("Erro ao salvar o paciente no banco de dados.");
                        }
                        fila.remove(paciente);
                        atualizarQtdPacientesPorFaixaEtaria(-1, paciente.getDataNascimento());
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado) {
                    System.out.println("❌ Fila Vazia! ");
                }
            }

            case 0 -> {
                if (!fila.isEmpty()) {
                    Paciente paciente = fila.poll();
                    atualizarQtdPacientesPorFaixaEtaria(-1, paciente.getDataNascimento());
                    System.out.println("╔════════════════════════════════════════════╗");
                    System.out.println("║           🏥 Paciente Atendido             ║");
                    System.out.println("╚════════════════════════════════════════════╝");
                    System.out.println("Nome: " + paciente.getNomeCompleto());
                    System.out.println("Senha: " + paciente.getSenha());
                    System.out.println("Tempo de espera: " + calcularTempoDePermanencia(paciente.getDataHoraEnfileiramento()));
                    paciente.setDataHoraDesenfileiramento(LocalDateTime.now());
                    pacientesAtendidos.add(paciente);
                    try {
                        atualizarBdo(pacientesAtendidos);
                    } catch (IOException e) {
                        System.out.println("Erro ao salvar o paciente no banco de dados.");
                    }
                    fila.remove(paciente);
                } else {
                    System.out.println("❌ Fila Vazia!");
                }
            }

            default -> System.out.println("❌ Prioridade inválida!");
        }

        try {
            atualizarBdo(fila);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void atualizarBdo(Collection<Paciente> colecao) throws IOException {
        LocalDate diaAtual = LocalDate.now();
        if (colecao instanceof PriorityQueue<?>) {
            try (FileOutputStream fileOutputStream = new FileOutputStream("src/bdo/fila-de-pacientes.txt");
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeObject(colecao);
            }
        } else if (colecao instanceof List<?>) {
            try (FileOutputStream fileOutputStream =
                         new FileOutputStream("src/bdo/pacientes-atendidos-data-" + diaAtual + ".txt");
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeObject(colecao);
            }
        } else {
            System.out.println("Insira um objeto do tipo PriorityQueue<Paciente> ou List<Paciente>");
        }
    }


    private static Collection<Paciente> carregarPacientesDoBdo(Collection<Paciente> colecao) throws IOException, ClassNotFoundException {
        Collection<Paciente> colecaoAtualizada = colecao;
        LocalDate diaAtual = LocalDate.now();
        if (colecao instanceof PriorityQueue<?>) {
            try (FileInputStream fileInputStream = new FileInputStream("src/bdo/fila-de-pacientes.txt");
                 ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                colecaoAtualizada = (PriorityQueue<Paciente>) objectInputStream.readObject();
            }
        } else if (colecao instanceof List<?>) {
            try (FileInputStream fileInputStream = new FileInputStream("src/bdo/pacientes-atendidos-data-" + diaAtual +
                    ".txt");
                 ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                colecaoAtualizada = (List<Paciente>) objectInputStream.readObject();
            }
        } else {
            System.out.println("Insira uma coleção do tipo PriorityQueue<Paciente> ou List<Paciente>");
        }
        return colecaoAtualizada;
    }


    private static String calcularTempoDePermanencia(LocalDateTime dataHoraEnfileiramento) {
        LocalDateTime agora = LocalDateTime.now();

        Duration duracao = Duration.between(dataHoraEnfileiramento, agora);

        long minutos = duracao.toMinutes();
        long segundos = duracao.getSeconds() % 60;

        return String.format("%d minutos e %d segundos", minutos, segundos);
    }

    private static int calcularIdade(LocalDate dataNascimento) {
        LocalDate dataAtual = LocalDate.now();
        return Period.between(dataNascimento, dataAtual).getYears();
    }


    //Passe 1 para acrescentar e -1 para diminuir
    private static void atualizarQtdPacientesPorFaixaEtaria(int acrescentarOuDiminuir, LocalDate dataNascimento) {
        int idade = calcularIdade(dataNascimento);
        int qtdAtual;

        if (acrescentarOuDiminuir == 1) {
            if (idade >= 0 && idade <= 11) {
                qtdAtual = qtdPacientesPorFaixaEtaria.get("CRIANÇAS");
                qtdPacientesPorFaixaEtaria.put("CRIANÇAS", qtdAtual + 1);
            } else if (idade >= 12 && idade <= 17) {
                qtdAtual = qtdPacientesPorFaixaEtaria.get("ADOLESCENTES");
                qtdPacientesPorFaixaEtaria.put("ADOLESCENTES", qtdAtual + 1);
            } else if (idade >= 18 && idade <= 59) {
                qtdAtual = qtdPacientesPorFaixaEtaria.get("ADULTOS");
                qtdPacientesPorFaixaEtaria.put("ADULTOS", qtdAtual + 1);
            } else if (idade >= 60) {
                qtdAtual = qtdPacientesPorFaixaEtaria.get("IDOSOS");
                qtdPacientesPorFaixaEtaria.put("IDOSOS", qtdAtual + 1);
            }

        } else if (acrescentarOuDiminuir == -1) {
            if (idade >= 0 && idade <= 11) {
                qtdAtual = qtdPacientesPorFaixaEtaria.get("CRIANÇAS");
                qtdPacientesPorFaixaEtaria.put("CRIANÇAS", qtdAtual - 1);
            } else if (idade >= 12 && idade <= 17) {
                qtdAtual = qtdPacientesPorFaixaEtaria.get("ADOLESCENTES");
                qtdPacientesPorFaixaEtaria.put("ADOLESCENTES", qtdAtual - 1);
            } else if (idade >= 18 && idade <= 59) {
                qtdAtual = qtdPacientesPorFaixaEtaria.get("ADULTOS");
                qtdPacientesPorFaixaEtaria.put("ADULTOS", qtdAtual - 1);
            } else if (idade >= 60) {
                qtdAtual = qtdPacientesPorFaixaEtaria.get("IDOSOS");
                qtdPacientesPorFaixaEtaria.put("IDOSOS", qtdAtual - 1);
            }
        }
    }
}