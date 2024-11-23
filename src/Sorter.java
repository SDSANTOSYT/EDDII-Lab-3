import java.io.Serializable;
import java.util.Stack;

public class Sorter {

    // Clase para representar el estado del algoritmo
    public static class MergeSortState implements Serializable {
        Stack<CallState> stateStack; // Pila para simular la recursión

        public MergeSortState(int arrayLength) {
            this.stateStack = new Stack<>();
            this.stateStack.push(new CallState(0, arrayLength - 1, false)); // Estado inicial
        }
    }

    // Clase para representar una llamada en la pila
    private static class CallState implements Serializable {
        int left, right;
        boolean mergeDone;
        int mid;

        public CallState(int left, int right, boolean mergeDone) {
            this.left = left;
            this.right = right;
            this.mergeDone = mergeDone;
            this.mid = left + (right - left) / 2;
        }
    }

    //MergeSort
//    public static void mergeSorts(int[] v, int n) {
//        //caso base arreglo es nada nas 1 dato
//        if (n < 2) {
//            return;
//        }
//        //encuentra el punto medio para hacer la subdivisión
//        int mid = n / 2;
//        //creacion de los arreglod temporales para divirlos
//        int[] left = new int[mid];
//        int[] right = new int[n - mid];
//        //dividiendo la mitad izquierda y la mitad derecha
//        for (int i = 0; i < mid; i++) {
//            left[i] = v[i];
//        }
//        for (int i = mid; i < n; i++) {
//            right[i - mid] = v[i];
//        }
//        //llamado recursivo a la función
//        mergeSorts(left, mid);
//        mergeSorts(right, n - mid);
//        //llamado a la funcion merge para volver a unir los arreglos
//        merges(v, left, right, mid, n - mid);
//
//    }
    public static void mergeSort(int[] array, MergeSortState state) {
        while (!state.stateStack.isEmpty()) {
            CallState current = state.stateStack.pop();

            if (!current.mergeDone) {
                if (current.left < current.right) {
                    int mid = current.mid;

                    // Dividir: Agregar estados a la pila
                    state.stateStack.push(new CallState(current.left, current.right, true)); // Para fusión
                    state.stateStack.push(new CallState(mid + 1, current.right, false));     // Derecha
                    state.stateStack.push(new CallState(current.left, mid, false));         // Izquierda
                }
            } else {
                // Fusionar las dos mitades
                merge(array, current.left, current.mid, current.right);
            }
        }
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




    //creación de la funcion merge para combinar los vectores temporales
//    public static void merges(int[] v, int[] left, int[] right, int limLeft, int limRight) {
//        int i = 0, j = 0, k = 0;
//
//        while (i < limLeft && j < limRight) {
//            //ordena los datos en orden ascendente y lo posiciona en el vector original
//            if (left[i] <= right[j]) {
//                v[k++] = left[i++];
//            } else {
//                v[k++] = right[j++];
//            }
//        }
//        //copia los otros datos del vector para unirlos de nuevo
//        while (i < limLeft) {
//            v[k++] = left[i++];
//        }
//        while (j < limRight) {
//            v[k++] = right[j++];
//        }
//    }

    //HeapSort
    public static void heapsort(int[] v) {
        int n = v.length;
        //Contruyamos un heap :D
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(v, n, i);
        }
        //Extraer elementos del heap uno por uno
        for (int i = n - 1; i >= 0; i--) {
            //Mover la raiz actual al final
            int temp = v[0];
            v[0] = v[i];
            v[i] = temp;

            heapify(v, i, 0);
        }
    }

    //Aqui construimos el heap
    static void heapify(int[] v, int n, int i) {
        int largest = i; //Nodo mas grande como raiz
        int l = 2 * i + 1; // izquierda
        int r = 2 * i + 2; // derecha

        //Si el hijo izquierdo es mas grande que la raiz
        if (l < n && v[l] > v[largest]) {
            largest = l;
        }
        //Si el hijo derecho es mas grande que el nodo mas grande hasta ahora
        if (r < n && v[r] > v[largest]) {
            largest = r;
        }

        //Si el nodo mas grande no es la raiz
        if (largest != i) {
            int swap = v[i];
            v[i] = v[largest];
            v[largest] = swap;

            heapify(v, n, largest);
        }
    }

    static void printArray(int[] v) {
        int n = v.length;
        for (int i = 0; i < n; i++) {
            System.out.print(v[i] + " ");
        }
    }

    //QuickSort
    private static int partition(int[] v, int low, int high) {
        //Ultimo elemento como pivote
        int pivot = v[high];
        int i = low - 1;

        //Itera sobre los elementos de la sublista
        for (int j = low; j < high; j++) {
            if (v[j] <= pivot) {
                //Si el elemento actual es menor o igual al pivote, incremento el indice menor
                i++;
                //Intercambia el elemento actual con el elemento en la posicion i
                int temp = v[i];
                v[i] = v[j];
                v[j] = temp;
            }
        }
        //Pivote en la posicion correcta :D
        int temp = v[i + 1];
        v[i + 1] = v[high];
        v[high] = temp;
        return i + 1; //Posicion final del pivote

    }

    public static void Aux(int[] v, int low, int high) {
        if (low < high) {
            //Verifica si la posicion del array tiene mas de un elemento
            int pivot = partition(v, low, high);
            Aux(v, low, pivot - 1);
            Aux(v, pivot + 1, high);
        }
    }

    public static void quickSort(int[] v) {
        Aux(v, 0, v.length - 1);
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
