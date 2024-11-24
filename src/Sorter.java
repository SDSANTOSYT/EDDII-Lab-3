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
        long startTime = System.currentTimeMillis();
        while (!state.stateStack.isEmpty()) {
            if ((System.currentTimeMillis() - startTime) >= maxTime * 1000L) {
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
    private static class HeapCallState implements Serializable{
        int index; //Indice actual para heapify
        int size;   //Tamaño del heap
        boolean heapifyDone;    // La heapificacion esta completa?

        HeapCallState(int index, int size,  boolean heapifyDone) {
          this.index = index;
          this.size = size;
          this.heapifyDone = heapifyDone;
        }
    }
    public static class HeapSortState implements Serializable{
        Stack<HeapCallState> stateStack = new Stack<>();
    }

    public static boolean heapSort(int[] array, HeapSortState state, float maxTime) {
       long startTime = System.currentTimeMillis();
       int n = array.length;

       for (int i = n/2 -1; i >= 0; i--) {
           state.stateStack.push(new HeapCallState(i,n,false));
       }

       while (!state.stateStack.isEmpty()) {
           if ((System.currentTimeMillis() - startTime) >= maxTime * 1000L) {
               return false;
           }else{
               HeapCallState current = state.stateStack.pop();

                   if(!current.heapifyDone){
                       heapify(array, current.index, current.size);
                       current.heapifyDone = true;
                       state.stateStack.push(current);
                   }
               }
           }
       for (int i = n-1; i>0 ; i--) {
           int temp = array[0];
           array[0] = array[i];
           array[i] = temp;

           state.stateStack.push(new HeapCallState(0,i,false));
           while(!state.stateStack.isEmpty()){
               if((System.currentTimeMillis() - startTime) >= maxTime * 1000L){
                   return false;
               }else{
                   HeapCallState current = state.stateStack.pop();
                   if(!current.heapifyDone){
                       heapify(array, current.index, current.size);
                       current.heapifyDone = true;
                       state.stateStack.push(current);
                   }
               }
           }
       }
       return true;
    }

    public static void heapify(int[] array, int root, int heapSize){
        int largest = root;
        int left = 2 * root + 1;
        int right = 2 * root + 2;

        if(left < heapSize && array[left] > array[largest]){
            largest = left;
        }
        if(right < heapSize && array[right] > array[largest]){
            largest = right;
        }
        if(largest != root){
            int temp = array[root];
            array[root] = array[largest];
            array[largest] = temp;

            heapify(array, largest, heapSize);
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
