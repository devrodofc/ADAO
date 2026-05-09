package analisealgoritmos.framework;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Classe utilitária responsável por gerar arrays de teste.
 * Implementa o padrão Factory para fornecer diferentes distribuições de dados.
 */
public final class DataGenerator {

    // Construtor privado para evitar instanciação de classe utilitária (Clean Code)
    private DataGenerator() {
        throw new UnsupportedOperationException("Esta é uma classe utilitária e não deve ser instanciada.");
    }

    /**
     * Enumera as diferentes naturezas de conjuntos de dados para examinar
     * o impacto no desempenho dos algoritmos.
     */
    public enum DataDistribution {
        RANDOM,        // Caso médio para a maioria dos algoritmos
        SORTED,        // Melhor caso para Insertion/Bubble, pior caso para QuickSort ingênuo
        REVERSED,      // Pior caso para Bubble, Insertion e Selection Sort
        NEARLY_SORTED  // Excelente para demonstrar a eficiência do Insertion Sort
    }

    /**
     * Gera um array de inteiros baseado no tamanho e na distribuição desejada.
     * * @param size O tamanho do array.
     * @param distribution A natureza da distribuição dos dados.
     * @return Um array de inteiros preenchido.
     */
    public static int[] generate(int size, DataDistribution distribution) {
        return switch (distribution) {
            case RANDOM -> generateRandom(size);
            case SORTED -> generateSorted(size);
            case REVERSED -> generateReversed(size);
            case NEARLY_SORTED -> generateNearlySorted(size);
        };
    }

    private static int[] generateRandom(int size) {
        // Uso de ThreadLocalRandom para alta performance e Thread-Safety
        // IntStream fornece uma maneira funcional e declarativa de construir o array
        return IntStream.generate(() -> ThreadLocalRandom.current().nextInt(0, 1_000_000))
                .limit(size)
                .toArray();
    }

    private static int[] generateSorted(int size) {
        // Gera um array do tipo: [0, 1, 2, 3, ..., size-1]
        return IntStream.range(0, size).toArray();
    }

    private static int[] generateReversed(int size) {
        // Gera um array do tipo: [size, size-1, ..., 2, 1]
        return IntStream.iterate(size, i -> i - 1)
                .limit(size)
                .toArray();
    }

    private static int[] generateNearlySorted(int size) {
        int[] array = generateSorted(size);
        // Bagunça aproximadamente 5% do array para criar o cenário "quase ordenado"
        int swaps = Math.max(1, size / 20);

        for (int i = 0; i < swaps; i++) {
            int idx1 = ThreadLocalRandom.current().nextInt(size);
            int idx2 = ThreadLocalRandom.current().nextInt(size);

            // Swap
            int temp = array[idx1];
            array[idx1] = array[idx2];
            array[idx2] = temp;
        }
        return array;
    }
}