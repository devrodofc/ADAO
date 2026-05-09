package analisealgoritmos.framework;

import analisealgoritmos.core.ExecutionResult;
import analisealgoritmos.core.Sorter;
import analisealgoritmos.framework.DataGenerator.DataDistribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Motor principal de execução dos testes de benchmark.
 * Orquestra as execuções, aplica aquecimento (warmup) na JVM e coleta as métricas.
 */
public class BenchmarkRunner {

    private static final int SAMPLES = 5; // Exigência do edital
    private static final int WARMUP_ITERATIONS = 3;

    private final List<Sorter> algorithms;
    private final int[] arraySizes;
    private final DataDistribution[] distributions;
    private final int[] threadCounts;

    public BenchmarkRunner(List<Sorter> algorithms, int[] arraySizes, DataDistribution[] distributions, int[] threadCounts) {
        this.algorithms = algorithms;
        this.arraySizes = arraySizes;
        this.distributions = distributions;
        this.threadCounts = threadCounts;
    }

    /**
     * Inicia a bateria de testes e retorna a lista com todos os resultados.
     */
    public List<ExecutionResult> runAll() {
        List<ExecutionResult> results = new ArrayList<>();

        System.out.println("Iniciando bateria de testes...");
        warmupJVM();

        for (DataDistribution distribution : distributions) {
            for (int size : arraySizes) {
                System.out.printf("\nGerando array base: Tamanho %,d | Distribuição: %s%n", size, distribution);
                // Gera o array base uma única vez para este tamanho e distribuição
                int[] baseArray = DataGenerator.generate(size, distribution);

                for (Sorter sorter : algorithms) {
                    System.out.printf("  -> Testando: %s%n", sorter.getName());

                    if (sorter.isParallel()) {
                        // Varia a quantidade de threads para algoritmos paralelos
                        for (int threads : threadCounts) {
                            runSamples(sorter, baseArray, size, distribution, threads, results);
                        }
                    } else {
                        // Execução serial (Thread = 1)
                        runSamples(sorter, baseArray, size, distribution, 1, results);
                    }
                }
            }
        }

        System.out.println("\nBateria de testes concluída!");
        return results;
    }

    private void runSamples(Sorter sorter, int[] baseArray, int size, DataDistribution distribution, int threads, List<ExecutionResult> results) {
        for (int sample = 1; sample <= SAMPLES; sample++) {
            // CLONE OBRIGATÓRIO: Garante que o algoritmo sempre receba os dados na mesma condição
            int[] arrayToVerify = Arrays.copyOf(baseArray, baseArray.length);

            // Sugere a limpeza de memória antes de cronometrar (minimiza impacto do GC no tempo)
            System.gc();

            long startTime = System.nanoTime();
            sorter.sort(arrayToVerify, threads);
            long endTime = System.nanoTime();

            long timeNanos = endTime - startTime;

            results.add(new ExecutionResult(
                    sorter.getName(),
                    sorter.isParallel(),
                    size,
                    distribution,
                    threads,
                    sample,
                    timeNanos
            ));
        }
    }

    /**
     * Roda algumas ordenações para "esquentar" a JVM e acionar o JIT Compiler,
     * garantindo que os tempos reais não sejam afetados pela compilação dinâmica.
     */
    private void warmupJVM() {
        System.out.println("Aquecendo a JVM (Warmup)...");
        int[] dummyArray = DataGenerator.generate(10000, DataDistribution.RANDOM);

        for (Sorter sorter : algorithms) {
            for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                int[] clone = Arrays.copyOf(dummyArray, dummyArray.length);
                sorter.sort(clone, Runtime.getRuntime().availableProcessors());
            }
        }
        System.gc(); // Limpa o lixo gerado no aquecimento
        System.out.println("Warmup concluído.");
    }
}