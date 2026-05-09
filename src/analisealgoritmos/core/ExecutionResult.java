package analisealgoritmos.core;

import analisealgoritmos.framework.DataGenerator.DataDistribution;

/**
 * Record que encapsula as métricas de uma única execução de ordenação.
 * Atualizado para suportar alta precisão e rastreabilidade da natureza dos dados.
 *
 * @param algorithmName      Nome do algoritmo testado.
 * @param isParallel         Indica se a execução foi paralela ou serial.
 * @param arraySize          Tamanho do conjunto de dados de entrada.
 * @param dataDistribution   A natureza/distribuição dos dados (ex: RANDOM, SORTED)[cite: 19].
 * @param threadCount        Número de threads utilizadas na execução.
 * @param sampleNumber       O número da amostra atual (1 a 5)[cite: 22].
 * @param executionTimeNanos O tempo total de ordenação em nanossegundos (System.nanoTime()).
 */
public record ExecutionResult(
        String algorithmName,
        boolean isParallel,
        int arraySize,
        DataDistribution dataDistribution,
        int threadCount,
        int sampleNumber,
        long executionTimeNanos
) {
    // Records são imutáveis por padrão, garantindo a integridade dos dados coletados.
}