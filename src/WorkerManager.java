import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Arrays;

public class WorkerManager implements Runnable {

    private final Socket socket;
    private final int workerId;
    private final String nextWorkerHost;
    private final int nextWorkerPort;
    private final String clientHost = "localhost";
    private final int clientPort = 5002;
    private SortingState state;

    public WorkerManager(Socket socket, int workerId, String nextWorkerHost, int nextWorkerPort) {
        this.socket = socket;
        this.workerId = workerId;
        this.nextWorkerHost = nextWorkerHost;
        this.nextWorkerPort = nextWorkerPort;
        this.state = null;
    }


    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            Object object = in.readObject();
            if (object instanceof SortingState) {
                this.state = (SortingState) object;
            } else {
                int[] vector = (int[]) object;
                int sortingMethod = in.readInt();
                //System.out.println(op);
                float maxTime = in.readFloat();
                //System.out.println(maxTime);
                boolean isSorted = in.readBoolean();
                this.state = new SortingState(vector, sortingMethod, maxTime);

            }
            int[] startArray = new int[this.state.vector.length];
            for (int i = 0; i < this.state.vector.length; i++) {
                startArray[i] = this.state.vector[i];
            }


            //System.out.println("Trabajador " + workerId + " - Comenzará a ordenar");

            if (!this.state.isSorted) {

                long startTime = 0;
                switch (state.sortingMethod) {
                    case 1:
                        startTime = System.currentTimeMillis();
                        state.isSorted = Sorter.mergeSort(state.vector, state.mergeSortState, state.maxTime);
                        break;
                    case 2:
                        startTime = System.currentTimeMillis();
                        state.isSorted = Sorter.quickSort(state.vector,state.quickSortState, state.maxTime);
                        break;
                    case 3:
                        startTime = System.currentTimeMillis();
                        state.isSorted = Sorter.heapSort(state.vector, state.heapSortState, state.maxTime);
                        break;
                    default:
                        System.out.println("Opcion fuera del menú: nunca deberás pasar por aqui");
                        break;
                }


                if (!state.isSorted) {
                    //System.out.println(Sorter.didArrayChange(this.state.vector, startArray));
                    //System.out.println("Trabajador " + workerId + " - Tiempo excedido, enviando a siguiente trabajador");
                    Socket clientSocket = new Socket(clientHost, clientPort);
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    out.writeObject("Trabajador " + workerId + " - Tiempo excedido, enviando a siguiente trabajador");
                    out.flush();
                    clientSocket.close();

                    Socket nextWorkerSocket = new Socket(nextWorkerHost, nextWorkerPort);
                    ObjectOutputStream outNextWorker = new ObjectOutputStream(nextWorkerSocket.getOutputStream());
                    outNextWorker.writeObject(this.state);
                    outNextWorker.flush();

                    nextWorkerSocket.close();
                } else {
                    long endTime = System.currentTimeMillis();
                    Arrays.sort(startArray);
                    System.out.println(Sorter.didArrayChange(this.state.vector, startArray));
                    //System.out.println("Trabajador " + workerId + " - Completó el ordenamiento");

                    SortingResult sortingResult = new SortingResult(this.state.vector, workerId, (float) ((endTime - startTime) / 1000.0));
                    Socket clientSocket = new Socket(clientHost, clientPort);
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    out.writeObject(sortingResult);
                    out.flush();

                }

            }
            socket.close();
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    public void sort(int[] array) {
        int[] array1 = array.clone();
        Arrays.sort(array1);
        for (int i = 0; i < array1.length; i++) {
            array[i] = array1[i];
        }
        //añadir metodos de ordenamiento
    }
}

class SortingResult implements Serializable {
    int[] vector;
    int workerId;
    float lasted;

    public SortingResult(int[] vector, int workerId, float lasted) {
        this.vector = vector;
        this.workerId = workerId;
        this.lasted = lasted;
    }
}

class SortingState implements Serializable {
    int[] vector;
    int sortingMethod;
    float maxTime;
    boolean isSorted;
    Sorter.MergeSortState mergeSortState;
    Sorter.HeapSortState heapSortState;
    Sorter.QuickSortState quickSortState;

    public SortingState(int[] vector, int sortingMethod, float maxTime) {
        this.vector = vector;
        this.sortingMethod = sortingMethod;
        this.maxTime = maxTime;
        this.isSorted = false;
        mergeSortState = new Sorter.MergeSortState(this.vector.length);
        heapSortState = new Sorter.HeapSortState(this.vector.length);
        quickSortState = new Sorter.QuickSortState(this.vector.length);
    }

}
