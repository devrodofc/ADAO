package analisealgoritmos.algorithms.parallel;

import analisealgoritmos.core.Sorter;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Implementação Paralela do Quick Sort utilizando o framework ForkJoin.
 * Inclui a otimização de Pivô Central para prevenir degradação de performance
 * e StackOverflowError em arrays previamente ordenados.
 */
public class QuickSortPar implements Sorter {

    @Override
    public void sort(int[] array, int numThreads) {
        ForkJoinPool pool = new ForkJoinPool(numThreads);
        pool.invoke(new QuickSortTask(array, 0, array.length - 1));
        pool.shutdown();
    }

    private static class QuickSortTask extends RecursiveAction {
        private final int[] array;
        private final int low;
        private final int high;
        private static final int THRESHOLD = 1000; // Limiar para fallback sequencial

        public QuickSortTask(int[] array, int low, int high) {
            this.array = array;
            this.low = low;
            this.high = high;
        }

        @Override
        protected void compute() {
            if (low < high) {
                if (high - low < THRESHOLD) {
                    // Fallback para ordenação sequencial em sub-arrays pequenos
                    sequentialQuickSort(array, low, high);
                } else {
                    int pi = partition(array, low, high);

                    // Dispara as duas metades em paralelo usando o ForkJoinPool
                    invokeAll(
                            new QuickSortTask(array, low, pi - 1),
                            new QuickSortTask(array, pi + 1, high)
                    );
                }
            }
        }

        private int partition(int[] array, int low, int high) {
            // Otimização do Pivô Central (Middle Pivot)
            // Previne o pior caso O(n^2) do Lomuto em arrays ordenados ou inversamente ordenados.
            int mid = low + (high - low) / 2;

            // Swap do elemento central com o último elemento
            int tempSwap = array[mid];
            array[mid] = array[high];
            array[high] = tempSwap;

            int pivot = array[high];
            int i = (low - 1);

            for (int j = low; j < high; j++) {
                if (array[j] < pivot) {
                    i++;
                    int temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }

            int temp = array[i + 1];
            array[i + 1] = array[high];
            array[high] = temp;

            return i + 1;
        }

        private void sequentialQuickSort(int[] array, int l, int h) {
            if (l < h) {
                int p = partition(array, l, h);
                sequentialQuickSort(array, l, p - 1);
                sequentialQuickSort(array, p + 1, h);
            }
        }
    }

    @Override
    public String getName() {
        return "Quick Sort Paralelo (ForkJoin)";
    }

    @Override
    public boolean isParallel() {
        return true;
    }
}