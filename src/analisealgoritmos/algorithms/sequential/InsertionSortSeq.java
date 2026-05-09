package analisealgoritmos.algorithms.sequential;

import analisealgoritmos.core.Sorter;

/**
 * Implementação Sequencial do Insertion Sort.
 * Eficiente para pequenos conjuntos de dados ou arrays quase ordenados.
 */
public class InsertionSortSeq implements Sorter {

    @Override
    public void sort(int[] array, int numThreads) {
        int n = array.length;
        for (int i = 1; i < n; ++i) {
            int key = array[i];
            int j = i - 1;

            // Move os elementos que são maiores que a 'key' para uma posição à frente
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j = j - 1;
            }
            array[j + 1] = key;
        }
    }

    @Override
    public String getName() {
        return "Insertion Sort Serial";
    }
}