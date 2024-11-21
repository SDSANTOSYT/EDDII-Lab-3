public class Sorter {

    //MergeSort
    public static void mergeSort(int[] v, int n) {
        //caso base arreglo es nada nas 1 dato
        if (n < 2) {
            return;
        }
        //encuentra el punto medio para hacer la subdivisión
        int mid = n / 2;
        //creacion de los arreglod temporales para divirlos
        int[] left = new int[mid];
        int[] right = new int[n - mid];
        //dividiendo la mitad izquierda y la mitad derecha
        for (int i = 0; i < mid; i++) {
            left[i] = v[i];
        }
        for (int i = mid; i < n; i++) {
            right[i - mid] = v[i];
        }
        //llamado recursivo a la función
        mergeSort(left, mid);
        mergeSort(right, n - mid);
        //llamado a la funcion merge para volver a unir los arreglos
        merge(v, left, right, mid, n - mid);

    }

    //creación de la funcion merge para combinar los vectores temporales
    public static void merge(int[] v, int[] left, int[] right, int limLeft, int limRight) {
        int i = 0, j = 0, k = 0;

        while (i < limLeft && j < limRight) {
            //ordena los datos en orden ascendente y lo posiciona en el vector original
            if (left[i] <= right[j]) {
                v[k++] = left[i++];
            } else {
                v[k++] = right[j++];
            }
            //copia los otros datos del vector para unirlos de nuevo
            while (i < limLeft) {
                v[k++] = left[i++];

            }
            while (j < limRight) {
                v[k++] = right[j++];
            }

        }
    }

   //HeapSort
   public static void heapsort(int[] v){
        int n = v.length;
        //Contruyamos un heap :D
        for (int i = n/2 -1; i >= 0; i--) {
            heapify(v,n,i);
        }
        //Extraer elementos del heap uno por uno
        for (int i = n-1; i >= 0; i--) {
            //Mover la raiz actual al final
            int temp = v[0];
            v[0] = v[i];
            v[i] = temp;

            heapify(v,i,0);
        }
   }
    //Aqui construimos el heap
    static void  heapify(int[] v, int n, int i) {
        int largest = i; //Nodo mas grande como raiz
        int l = 2*i + 1; // izquierda
        int r = 2*i + 2; // derecha

       //Si el hijo izquierdo es mas grande que la raiz
        if(l<n && v[l]>v[largest]){
            largest = l;
        }
        //Si el hijo derecho es mas grande que el nodo mas grande hasta ahora
        if(r<n && v[r]>v[largest]){
            largest = r;
        }

        //Si el nodo mas grande no es la raiz
        if(largest != i){
            int swap = v[i];
            v[i] = v[largest];
            v[largest] = swap;

            heapify(v,n,largest);
        }
   }

   static void printArray(int[] v){
        int n = v.length;
        for (int i = 0; i < n; i++) {
            System.out.print(v[i]+" ");
        }
   }

   //QuickSort
    private  static int partition(int[] v, int low, int high) {
        //Ultimo elemento como pivote
        int pivot = v[low];
        int i = low-1;

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
        int temp = v[i+1];
        v[i+1] = v[high];
        v[high] = temp;
        return i+1; //Posicion final del pivote

    }
    public static void Aux(int []v, int low, int high) {
        if (low < high) {
            //Verifica si la posicion del array tiene mas de un elemento
            int pivot = partition(v, low, high);
            if (pivot - low < high - pivot) {
                Aux(v, low, pivot - 1);
                low = pivot + 1;
            }else{
                Aux(v, pivot +1, high);
                high = pivot - 1;
            }
        }
    }

    public static void quickSort(int[] v) {
        Aux(v, 0, v.length - 1);
    }
}
