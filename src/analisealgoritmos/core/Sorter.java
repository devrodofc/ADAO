package analisealgoritmos.core;

/**
 * Contrato base para todos os algoritmos de ordenação (Strategy Pattern).
 */
public interface Sorter {

    /**
     * Executa o algoritmo de ordenação no array fornecido (ordenação in-place).
     *
     * @param array O vetor de inteiros a ser ordenado. A ordenação deve modificar este array diretamente.
     * @param numThreads O número de threads a ser utilizado.
     * Nota: Implementações seriais devem simplesmente ignorar este parâmetro.
     */
    void sort(int[] array, int numThreads);

    /**
     * Retorna o nome do algoritmo (ex: "Quick Sort Serial", "Merge Sort Paralelo").
     * Fundamental para a rastreabilidade na geração do arquivo CSV e análise de desempenho.
     *
     * @return O nome identificador do algoritmo.
     */
    String getName();

    /**
     * Indica se a implementação é paralela ou serial.
     * Útil para o framework de testes decidir se deve variar as threads durante a execução.
     * * @return true se for uma implementação paralela, false caso contrário.
     */
    default boolean isParallel() {
        return false;
    }
}