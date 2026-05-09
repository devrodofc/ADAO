package analisealgoritmos.algorithms.sequential;

import analisealgoritmos.core.Sorter;

public class QuickSortSeq implements Sorter {

    @Override
    public void sort(int[] array, int numThreads) {
        quickSort(array, 0, array.length - 1);
    }

    private void quickSort(int[] array, int low, int high) {
        if (low < high) {
            int pi = partition(array, low, high);
            quickSort(array, low, pi - 1);
            quickSort(array, pi + 1, high);
        }
    }

    private int partition(int[] array, int low, int high) {
        int mid = low + (high - low) / 2;

        // Swap do elemento central com o último elemento.
        // Justificativa para o relatório: Esta manobra evita o erro de java.lang.StackOverflowError
        // em arrays que já estão ordenados (ou quase ordenados). Ao evitar que o maior elemento
        // seja sempre o pivô em partições já ordenadas, quebramos a degradação para o pior caso O(n^2)
        // e garantimos um balanceamento muito mais saudável da árvore de recursão da pilha.
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

    @Override
    public String getName() {
        return "Quick Sort Serial";
    }
}