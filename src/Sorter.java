public class Sorter {

    //MergeSort
    public static void mergeSort(int[] v, int n){
        //caso base arreglo es nada nas 1 dato
        if(n<2){
            return;
        }
        //encuentra el punto medio para hacer la subdivisión
        int mid = n/2;
        //creacion de los arreglod temporales para divirlos
        int[] left= new int[mid];
        int[] right=new int[n-mid];
        //dividiendo la mitad izquierda y la mitad derecha
        for (int i = 0; i < mid; i++) {
            left[i]=v[i];
        }
        for (int i = mid; i < n; i++) {
            right[i-mid]=v[i];
        }
        //llamado recursivo a la función
        mergeSort(left,mid);
        mergeSort(right,n-mid);
        //llamado a la funcion merge para volver a unir los arreglos
        merge(v,left,right,mid,n-mid);

    }
    //creación de la funcion merge para combinar los vectores temporales
    public static void merge(int[] v, int[] left, int[]right, int limLeft, int limRight){
        int i=0,j=0,k=0;

        while (i<limLeft && j<limRight){
            //ordena los datos en orden ascendente y lo posiciona en el vector original
            if(left[i]<=right[j]){
                v[k++]=left[i++];
            }else{
                v[k++]=right[j++];
            }
            //copia los otros datos del vector para unirlos de nuevo
            while(i<limLeft){
                v[k++]=left[i++];

            }
            while(j<limRight){
                v[k++]=right[j++];
            }

        }
    }
}
