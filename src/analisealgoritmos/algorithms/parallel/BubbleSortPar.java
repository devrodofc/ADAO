package analisealgoritmos.algorithms.parallel;

import analisealgoritmos.core.Sorter;

import java.util.concurrent.Phaser;

/**
 * Implementação Paralela do Bubble Sort utilizando o algoritmo Odd-Even Transposition.
 * Utiliza um Phaser para sincronizar as fases de comparação Par e Ímpar.
 */
public class BubbleSortPar implements Sorter {

    @Override
    public void sort(int[] array, int numThreads) {
        if (array.length <= 1) return;

        // Limita o número de threads ao tamanho do array pela metade (não precisamos de mais threads que pares)
        int effectiveThreads = Math.min(numThreads, array.length / 2);
        if (effectiveThreads <= 1) {
            // Se for 1 thread ou array muito pequeno, faz um fallback para o Bubble clássico
            new analisealgoritmos.algorithms.sequential.BubbleSortSeq().sort(array, 1);
            return;
        }

        Phaser phaser = new Phaser(effectiveThreads);
        Thread[] threads = new Thread[effectiveThreads];

        for (int i = 0; i < effectiveThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> workerTask(array, effectiveThreads, threadId, phaser));
            threads[i].start();
        }

        // Aguarda todas as threads terminarem
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("A execução paralela foi interrompida.", e);
            }
        }
    }

    private void workerTask(int[] array, int numThreads, int threadId, Phaser phaser) {
        int n = array.length;

        // Cada thread é responsável por um "chunk" (pedaço) do array
        int chunkSize = (n + numThreads - 1) / numThreads;
        int start = threadId * chunkSize;
        int end = Math.min(start + chunkSize, n);

        for (int phase = 0; phase < n; phase++) {
            if (phase % 2 == 0) { // Fase Par
                for (int i = start + (start % 2 == 0 ? 0 : 1); i < end - 1; i += 2) {
                    if (array[i] > array[i + 1]) {
                        swap(array, i, i + 1);
                    }
                }
            } else { // Fase Ímpar
                for (int i = start + (start % 2 != 0 ? 0 : 1); i < end - 1; i += 2) {
                    if (array[i] > array[i + 1]) {
                        swap(array, i, i + 1);
                    }
                }
            }
            // Barreira: Nenhuma thread avança para a próxima fase até que todas terminem a atual
            phaser.arriveAndAwaitAdvance();
        }
        phaser.arriveAndDeregister();
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    @Override
    public String getName() {
        return "Bubble Sort Paralelo (Odd-Even)";
    }

    @Override
    public boolean isParallel() {
        return true;
    }
}