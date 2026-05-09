package analisealgoritmos;

import analisealgoritmos.algorithms.parallel.*;
import analisealgoritmos.algorithms.sequential.*;
import analisealgoritmos.core.ExecutionResult;
import analisealgoritmos.core.Sorter;
import analisealgoritmos.framework.BenchmarkRunner;
import analisealgoritmos.framework.DataGenerator.DataDistribution;
import analisealgoritmos.io.CSVExporter;
import analisealgoritmos.view.DashboardFrame;

import java.util.Arrays;
import java.util.List;

/**
 * Ponto de entrada do sistema.
 * Configura os cenários de teste exigidos pelo projeto acadêmico.
 */
public class Main {
  public static void main(String[] args) {
    long programStart = System.nanoTime();
    // 1. Definição dos algoritmos a serem testados (Atendendo aos requisitos do trabalho)
    // Selecionamos 4 algoritmos conforme solicitado no item 1 dos Objetivos
    List<Sorter> algorithms = Arrays.asList(
            new BubbleSortSeq(),
            new BubbleSortPar(),
            new InsertionSortSeq(),
            new InsertionSortPar(),
            new QuickSortSeq(),
            new QuickSortPar(),
            new MergeSortSeq(),
            new MergeSortPar()
    );

    // 2. Configuração das variáveis de ambiente de teste
    // Variando o tamanho dos conjuntos de dados
    int[] arraySizes = {1000, 5000, 10000, 25000, 50000};

    // Variando a natureza dos dados (RANDOM, SORTED, etc.) [cite: 19, 39]
    DataDistribution[] distributions = {
            DataDistribution.RANDOM,
            DataDistribution.NEARLY_SORTED,
            DataDistribution.REVERSED
    };

    // Variando a quantidade de threads para execuções paralelas [cite: 21, 25]
    int cores = Runtime.getRuntime().availableProcessors();
    int[] threadCounts = {2, 4, cores}; // Testa com 2, 4 e o máximo do hardware atual

    // 3. Inicialização do Framework de Teste [cite: 38]
    BenchmarkRunner runner = new BenchmarkRunner(
            algorithms,
            arraySizes,
            distributions,
            threadCounts
    );

    // 4. Execução da bateria de testes e coleta de resultados (Garantindo 5 amostras cada) [cite: 22]
    List<ExecutionResult> allResults = runner.runAll();

    // 5. Exportação dos dados para análise visual em CSV [cite: 26, 47]
    CSVExporter exporter = new CSVExporter();
    String outputFileName = "analise_desempenho_algoritmos.csv";
    exporter.export(allResults, outputFileName);
    DashboardFrame window = new DashboardFrame("Análise de Desempenho");
    allResults.forEach(window::addResult);
    window.setVisible(true);

    // Captura o fim da execução do programa
    long programEnd = System.nanoTime();
    long totalProgramTimeNanos = programEnd - programStart;

    System.out.println("\n--- RELATÓRIO FINAL DE TEMPO DE EXECUÇÃO ---");

// 1. Tempo total do programa (do início ao fim do main)
    System.out.printf("Tempo Total de Execução do Programa: %.2f segundos%n",
            totalProgramTimeNanos / 1_000_000_000.0);

// 2. Tempo acumulado gasto por cada algoritmo (Soma de todas as amostras e cenários)
    System.out.println("\nTempo acumulado por Algoritmo (Total de todas as amostras):");
    allResults.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                    ExecutionResult::algorithmName,
                    java.util.stream.Collectors.summingLong(ExecutionResult::executionTimeNanos)
            ))
            .forEach((name, totalNanos) -> {
              System.out.printf("- %-35s: %.4f ms%n", name, totalNanos / 1_000_000.0);
            });

    System.out.println("\n====================================================");
    System.out.println("Processo concluído com sucesso!");
    System.out.println("Use o arquivo '" + outputFileName + "' para gerar os gráficos.");
    System.out.println("====================================================");

  }
}