import java.io.Serializable;
import java.util.Stack;

public class Sorter {

    // Clase para representar el estado del algoritmo
    public static class MergeSortState implements Serializable {
        Stack<MergeCallState> stateStack; // Pila para simular la recursión

        public MergeSortState(int arrayLength) {
            this.stateStack = new Stack<>();
            this.stateStack.push(new MergeCallState(0, arrayLength - 1, false)); // Estado inicial
        }
    }

    // Clase para representar una llamada en la pila
    private static class MergeCallState implements Serializable {
        int left, right;
        boolean mergeDone;
        int mid;

        public MergeCallState(int left, int right, boolean mergeDone) {
            this.left = left;
            this.right = right;
            this.mergeDone = mergeDone;
            this.mid = left + (right - left) / 2;
        }
    }

    //MergeSort
    public static boolean mergeSort(int[] array, MergeSortState state, float maxTime) {
        long startTime = System.nanoTime();
        while (!state.stateStack.isEmpty()) {
            if ((System.nanoTime() - startTime) >= maxTime * 1000000000L) {
                return false;
            } else {
                MergeCallState current = state.stateStack.pop();

                if (!current.mergeDone) {
                    if (current.left < current.right) {
                        int mid = current.mid;

                        // Dividir: Agregar estados a la pila
                        state.stateStack.push(new MergeCallState(current.left, current.right, true)); // Para fusión
                        state.stateStack.push(new MergeCallState(mid + 1, current.right, false));     // Derecha
                        state.stateStack.push(new MergeCallState(current.left, mid, false));         // Izquierda
                    }
                } else {
                    // Fusionar las dos mitades
                    merge(array, current.left, current.mid, current.right);
                }
            }

        }
        return true;
    }

    // Método de fusión
    private static void merge(int[] array, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];

        System.arraycopy(array, left, L, 0, n1);
        System.arraycopy(array, mid + 1, R, 0, n2);

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                array[k++] = L[i++];
            } else {
                array[k++] = R[j++];
            }
        }

        while (i < n1) {
            array[k++] = L[i++];
        }

        while (j < n2) {
            array[k++] = R[j++];
        }
    }

    //HeapSort
    private static class HeapCallState implements Serializable {
        int index; //Indice actual para heapify
        int size;   //Tamaño del heap

        HeapCallState(int index, int size) {
            this.index = index;
            this.size = size;
        }
    }

    public static class HeapSortState implements Serializable {
        Stack<HeapCallState> stateStack = new Stack<>();
        boolean heapifyDone = false; // La heapificación esta completa?

        public HeapSortState(int arrayLength) {
            stateStack.push(new HeapCallState(arrayLength / 2 - 1, arrayLength));
        }

    }

    public static boolean heapSort(int[] array, HeapSortState state, float maxTime) {
        long startTime = System.nanoTime();
        int n = array.length;

        while (!state.stateStack.isEmpty()) {
            if ((System.nanoTime() - startTime) >= maxTime * 1000000000L) {
                return false;
            } else {
                HeapCallState current = state.stateStack.pop();
                if (!state.heapifyDone) {
                    heapify(array, current.index, current.size);
                    if (current.index-- > 0) {
                        state.stateStack.push(new HeapCallState(current.index--, n));
                    } else {
                        state.heapifyDone = true;
                        state.stateStack.push(new HeapCallState(0, n - 1));
                    }
                } else {
                    int temp = array[0];
                    array[0] = array[current.size];
                    array[current.size] = temp;
                    heapify(array, current.index, current.size);
                    if (current.size-- > 0) {
                        state.stateStack.push(new HeapCallState(0, current.size--));
                    }
                }
            }
        }
        return true;
    }

    public static void heapify(int[] array, int root, int heapSize) {
        int largest = root;
        int left = 2 * root + 1;
        int right = 2 * root + 2;

        if (left < heapSize && array[left] > array[largest]) {
            largest = left;
        }
        if (right < heapSize && array[right] > array[largest]) {
            largest = right;
        }
        if (largest != root) {
            int temp = array[root];
            array[root] = array[largest];
            array[largest] = temp;

            heapify(array, largest, heapSize);
        }
    }

    //QuickSort
    public static class QuickSortState implements Serializable {
        Stack<QuickSortCallState> stateStack;

        public QuickSortState(int n) {
            this.stateStack = new Stack<>();
            stateStack.push(new QuickSortCallState(0,n- 1));
        }
    }

    private static class QuickSortCallState implements Serializable {
        int left, right;

        public QuickSortCallState(int left, int right) {
            this.left = left;
            this.right = right;
        }
    }

    public static boolean quickSort(int[] array, QuickSortState state, float maxTime) {
        long startTime = System.nanoTime();

        while (!state.stateStack.isEmpty()) {
            if ((System.nanoTime() - startTime) >= maxTime * 1000000000L) {
                return false;
            }else{
                QuickSortCallState current = state.stateStack.pop();
                int left = current.left;
                int right = current.right;

                if (left < right) {
                    int pivotIndex = partition(array,left,right);
                    state.stateStack.push(new QuickSortCallState(left, pivotIndex - 1));
                    state.stateStack.push(new QuickSortCallState(pivotIndex + 1, right));
                }
            }
        }
        return true;
    }
    private static int partition(int[] array, int left, int right) {
        int pivot = array[right];
        int i = left - 1;

        for(int j = left; j< right; j++) {
            if (array[j] <= pivot) {
                i++;
                swap(array,i,j);
            }
        }
        swap(array, i+1, right);
        return i+1;
    }

    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }


    public static boolean didArrayChange(int[] v1, int[] v2) {
        for (int i = 0; i < v1.length; i++) {
            if (v2[i] != v1[i]) {
                return true;
            }
        }
        return false;
    }
}
