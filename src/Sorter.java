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
                        state.stateStack.push(new MergeCallState(mid + 1, current.right, false)); // Derecha
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
        //Calcular el tamaño de los subarrays
        int n1 = mid - left + 1;    //Tamaño del subrray izquierdo
        int n2 = right - mid;   //Tamaño del subrray derecho

        //Crear array's temporales para almacenar los elementos de los subarrays
        int[] L = new int[n1];  //Subarray izquierdo
        int[] R = new int[n2];  //Subarray derecho

        //Copiar los elementos del array original a los subarrays temporales
        System.arraycopy(array, left, L, 0, n1);    //Copiar el subarray izquierdo
        System.arraycopy(array, mid + 1, R, 0, n2); //Copiar el subarray derecho

        //Inicializar indices para recorrer los subarrays y el array original
        int i = 0, j = 0, k = left;
        //Fusionar los subarrays en el array original
        while (i < n1 && j < n2) {//Mientras haya elementos en ambos subarrays
            if (L[i] <= R[j]) {//Comparar los elementos actuales de L y R
                array[k++] = L[i++]; //Si L[i] es menor o igual, agregarlo al array original
            } else {
                array[k++] = R[j++];// Si R[j] es menor, agregarlo al array original
            }
        }
        //Copiar los elementos restantes del subarray izquierdo, si los hay
        while (i < n1) {
            array[k++] = L[i++]; //Agregar los elementos restante de L al array original
        }

        //Copiar los elementos restante del subarray derecho, si los hay
        while (j < n2) {
            array[k++] = R[j++];//Agregar los elementos restantes de R al array original
        }
    }

    //HeapSort

    // Clase que representa un estado del HeapSort
    private static class HeapCallState implements Serializable {
        int index; //Indice actual para heapify
        int size;   //Tamaño del heap
        //Inicialicemos el indice y el tamaño del heap
        HeapCallState(int index, int size) {
            this.index = index;
            this.size = size;
        }
    }

    // Clase que guarda los estados del HeapSort
    public static class HeapSortState implements Serializable {
        Stack<HeapCallState> stateStack = new Stack<>(); //Pila para alamacenar los estados
        boolean heapifyDone = false; // La heapificación esta completa?

        //Constructor que inicializa el estado del heapsort
        public HeapSortState(int arrayLength) {
            //Inicializa la pila con el indice del ultimo nodo no hoja
            stateStack.push(new HeapCallState(arrayLength / 2 - 1, arrayLength));
        }

    }

    //Método de ordenamiento HeapSort
    public static boolean heapSort(int[] array, HeapSortState state, float maxTime) {
        long startTime = System.nanoTime(); //Guarda el tiempo inicial
        int n = array.length; //Obtener la longitud del array

        //Bucle que continua mientras haya estados en la pila
        while (!state.stateStack.isEmpty()) {
            //Comprobamos is se ha excedido el tiempo maximo
            if ((System.nanoTime() - startTime) >= maxTime * 1000000000L) {
                return false; //Si nos pasamos, salimos
            } else {
                //Cual es el estado de la pila?
                HeapCallState current = state.stateStack.pop();
                //Si la hapificacion no ha terminado
                if (!state.heapifyDone) {
                    //REaliza la operacion de heapify en el indice actual
                    heapify(array, current.index, current.size);
                    //Si hay mas nodos no hoja para heapify
                    if (current.index-- > 0) {
                        //Agregar el siguiente estado a la pila
                        state.stateStack.push(new HeapCallState(current.index--, n));
                    } else {
                        //Si se ha termido de heapificar, esta ready
                        state.heapifyDone = true;
                        //Agregar el estado para comenzar a ordenar
                        state.stateStack.push(new HeapCallState(0, n - 1));
                    }
                } else {
                    //Intercambiar el primer elemento (maximo) con el ultimo elemento del heap
                    int temp = array[0];
                    array[0] = array[current.size]; //Mover el ultimo elemento al inicio
                    array[current.size] = temp; //Colocar el maximo en su posicion final
                    //Realizar heapify en la nueva root
                    heapify(array, current.index, current.size);
                    //Si hay mas elemento en el heap
                    if (current.size-- > 0) {
                        //Agregar el siguiente estado a la pila
                        state.stateStack.push(new HeapCallState(0, current.size--));
                    }
                }
            }
        }
        return true; //Ready
    }

    // Método Heapify para el HeapSort
    public static void heapify(int[] array, int root, int heapSize) {
        int largest = root; //Incializar el mayor como root
        int left = 2 * root + 1; //Indice del hijo izquierdo
        int right = 2 * root + 2; //Indice del hijo derecho

        //Comprobar si el hijo izquierdo es mayor que el root
        if (left < heapSize && array[left] > array[largest]) {
            largest = left;//Actualizar el mayor
        }

        //Comprobar si el hijo derecho es mayor que el mayor actual
        if (right < heapSize && array[right] > array[largest]) {
            largest = right;
        }
        //Si el mayor no es el root, intercambiar y continuar el heapify
        if (largest != root) {
            int temp = array[root]; //Almacenar el valor del root
            array[root] = array[largest];//Mover el mayor al root
            array[largest] = temp; //Colocar el root en la posicion del mayor

            //Heapificamos el subarbol afectado
            heapify(array, largest, heapSize);
        }
    }

    //QuickSort
    public static class QuickSortState implements Serializable {
        //Pila para almacenar el estado
        Stack<QuickSortCallState> stateStack;

        //Inicializa la pila con el rango completo del array
        public QuickSortState(int n) {
            this.stateStack = new Stack<>();
            stateStack.push(new QuickSortCallState(0, n - 1));
        }
    }
    //Llamemos a la pila
    private static class QuickSortCallState implements Serializable {
        int left, right;
        //Incicializar los indices derecho y izquierdo
        public QuickSortCallState(int left, int right) {
            this.left = left;
            this.right = right;
        }
    }

    public static boolean quickSort(int[] array, QuickSortState state, float maxTime) {
        long startTime = System.nanoTime();
        //Bucle mientras que haya estados en la pila
        while (!state.stateStack.isEmpty()) {
            //Comprobar si se ha excedido el tiempo maximo permitido
            if ((System.nanoTime() - startTime) >= maxTime * 1000000000L) {
                return false; //Salir si se ha excedido el timpo maximo
            } else {
                //Obtener el estado actual de la pila
                QuickSortCallState current = state.stateStack.pop();
                int left = current.left;//Obtener indice izquierdo
                int right = current.right; //Obtener indice derecho

                //Si hay un rango valido para ordenar
                if (left < right) {
                    //Realizar la particion y obtener el indice del pivote
                    int pivotIndex = partition(array, left, right);

                    //Agregar los subrangos a la pila para ser procesados
                    state.stateStack.push(new QuickSortCallState(left, pivotIndex - 1));
                    state.stateStack.push(new QuickSortCallState(pivotIndex + 1, right));
                }
            }
        }
        return true; //Ordenamiento ready!
    }
    //Metodo para partir el array
    private static int partition(int[] array, int left, int right) {
        int pivot = array[right];// Elegimos el pivote (ultimo elemento)
        int i = left - 1; // Indice del elemento mas pequeño

        //Se reorganiza el array en torno al pivote
        for (int j = left; j < right; j++) {
            if (array[j] <= pivot) {
                i++; //Incrementamos el indice del elemento para pequeño
                swap(array, i, j); //Intercambiamos elementos
            }
        }
        swap(array, i + 1, right); //Movemos el pivote a su posicion adecuada
        return i + 1; //Y damos el indice del pivote
    }

    //Metodo para intercambiar dos elementos en el array
    private static void swap(int[] array, int i, int j) {
        int temp = array[i]; //Almacenar temporalmente el valor del array[i]
        array[i] = array[j];//Asigna el valor de array[j] a array[i]
        array[j] = temp; //Asigna el valor temporal a array [j]
    }

    //Verificar si el array realmente se ordeno
    public static boolean didArrayChange(int[] v1, int[] v2) {
        for (int i = 0; i < v1.length; i++) {
            if (v2[i] != v1[i]) {
                return true;
            }
        }
        return false;
    }
}
