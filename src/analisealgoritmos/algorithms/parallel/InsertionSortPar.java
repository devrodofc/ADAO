package analisealgoritmos.algorithms.parallel;

import analisealgoritmos.core.Sorter;
import analisealgoritmos.algorithms.sequential.InsertionSortSeq;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.Arrays;

public class InsertionSortPar implements Sorter {

    @Override
    public void sort(int[] array, int numThreads) {
        if (array.length <= 1) return;

        ForkJoinPool pool = new ForkJoinPool(numThreads);
        pool.invoke(new ParallelInsertionTask(array, 0, array.length, numThreads));
        pool.shutdown();
    }

    private static class ParallelInsertionTask extends RecursiveAction {
        private final int[] array;
        private final int start;
        private final int end;
        private final int threadsDisponiveis;

        ParallelInsertionTask(int[] array, int start, int end, int threads) {
            this.array = array;
            this.start = start;
            this.end = end;
            this.threadsDisponiveis = threads;
        }

        @Override
        protected void compute() {
            if (threadsDisponiveis <= 1 || (end - start) < 1000) {
                // Caso base: ordena o segmento sequencialmente
                insertionSort(array, start, end);
            } else {
                int mid = start + (end - start) / 2;
                int threadsMetade = threadsDisponiveis / 2;

                // Divide em duas tarefas
                invokeAll(
                        new ParallelInsertionTask(array, start, mid, threadsMetade),
                        new ParallelInsertionTask(array, mid, end, threadsDisponiveis - threadsMetade)
                );

                // Intercala os dois segmentos ordenados (Merge)
                merge(array, start, mid, end);
            }
        }

        private void insertionSort(int[] arr, int left, int right) {
            for (int i = left + 1; i < right; i++) {
                int key = arr[i];
                int j = i - 1;
                while (j >= left && arr[j] > key) {
                    arr[j + 1] = arr[j];
                    j--;
                }
                arr[j + 1] = key;
            }
        }

        private void merge(int[] arr, int left, int mid, int right) {
            int[] leftArr = Arrays.copyOfRange(arr, left, mid);
            int[] rightArr = Arrays.copyOfRange(arr, mid, right);

            int i = 0, j = 0, k = left;
            while (i < leftArr.length && j < rightArr.length) {
                arr[k++] = (leftArr[i] <= rightArr[j]) ? leftArr[i++] : rightArr[j++];
            }
            while (i < leftArr.length) arr[k++] = leftArr[i++];
            while (j < rightArr.length) arr[k++] = rightArr[j++];
        }
    }

    @Override
    public String getName() {
        return "Insertion Sort Paralelo (ForkJoin)";
    }

    @Override
    public boolean isParallel() {
        return true;
    }
}