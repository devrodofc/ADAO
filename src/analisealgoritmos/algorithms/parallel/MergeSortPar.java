package analisealgoritmos.algorithms.parallel;

import analisealgoritmos.core.Sorter;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class MergeSortPar implements Sorter {

    @Override
    public void sort(int[] array, int numThreads) {
        // Criamos um pool com o número exato de threads definido no benchmark
        ForkJoinPool pool = new ForkJoinPool(numThreads);
        pool.invoke(new MergeSortTask(array, 0, array.length - 1));
        pool.shutdown();
    }

    private static class MergeSortTask extends RecursiveAction {
        private final int[] array;
        private final int left;
        private final int right;
        private static final int THRESHOLD = 1000; // Evita overhead de threads para pedaços pequenos

        public MergeSortTask(int[] array, int left, int right) {
            this.array = array;
            this.left = left;
            this.right = right;
        }

        @Override
        protected void compute() {
            if (right - left < THRESHOLD) {
                // Caso base: Ordena sequencialmente se o sub-array for pequeno
                sequentialMergeSort(array, left, right);
            } else {
                int mid = left + (right - left) / 2;

                // Divide as tarefas e as submete ao ForkJoinPool
                MergeSortTask leftTask = new MergeSortTask(array, left, mid);
                MergeSortTask rightTask = new MergeSortTask(array, mid + 1, right);

                // fork() envia a primeira tarefa para a fila;
                // invokeAll(leftTask, rightTask) é uma alternativa mais limpa:
                invokeAll(leftTask, rightTask);

                merge(array, left, mid, right);
            }
        }

        // Método de merge idêntico ao serial para consistência
        private void merge(int[] array, int left, int mid, int right) {
            int n1 = mid - left + 1;
            int n2 = right - mid;
            int[] L = new int[n1];
            int[] R = new int[n2];
            System.arraycopy(array, left, L, 0, n1);
            System.arraycopy(array, mid + 1, R, 0, n2);
            int i = 0, j = 0, k = left;
            while (i < n1 && j < n2) {
                if (L[i] <= R[j]) array[k++] = L[i++];
                else array[k++] = R[j++];
            }
            while (i < n1) array[k++] = L[i++];
            while (j < n2) array[k++] = R[j++];
        }

        private void sequentialMergeSort(int[] array, int left, int right) {
            if (left < right) {
                int mid = left + (right - left) / 2;
                sequentialMergeSort(array, left, mid);
                sequentialMergeSort(array, mid + 1, right);
                merge(array, left, mid, right);
            }
        }
    }

    @Override
    public String getName() {
        return "Merge Sort Paralelo (ForkJoin)";
    }

    @Override
    public boolean isParallel() {
        return true;
    }
}