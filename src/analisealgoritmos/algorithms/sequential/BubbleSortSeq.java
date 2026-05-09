package analisealgoritmos.algorithms.sequential;

import analisealgoritmos.core.Sorter;

/**
 * Implementação Sequencial do Bubble Sort.
 * Possui otimização de parada antecipada se não houver trocas na iteração.
 */
public class BubbleSortSeq implements Sorter {

    @Override
    public void sort(int[] array, int numThreads) {
        int n = array.length;
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    // Swap
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = true;
                }
            }
            // Se não houve nenhuma troca nesta passagem, o array já está ordenado
            if (!swapped) break;
        }
    }

    @Override
    public String getName() {
        return "Bubble Sort Serial";
    }
}