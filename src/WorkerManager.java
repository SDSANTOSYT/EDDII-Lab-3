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
            // Se obtiene el input del socket que se conectó al worker
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            Object object = in.readObject();
            // Se verifica si se va a continuar ordenando o es la primera vez que se va a ordenar
            if (object instanceof SortingState) {
                this.state = (SortingState) object;
            } else {
                int[] vector = (int[]) object; // Se lee el vector
                int sortingMethod = in.readInt(); // Se lee el método de ordenamiento deseado
                float maxTime = in.readFloat(); // Se lee el tiempo máximo
                this.state = new SortingState(vector, sortingMethod, maxTime, 0);

            }
            int[] startArray = new int[this.state.vector.length];
            for (int i = 0; i < this.state.vector.length; i++) {
                startArray[i] = this.state.vector[i];
            }

            // Se imprime el estado del worker
            System.out.println("Trabajador " + workerId + " - Recibido");
            System.out.println("Trabajador " + workerId + " - Comenzará a ordenar");

            if (!this.state.isSorted) {
                // Se empieza el proceso de ordenamiento
                long startTime = 0; // Se inicializa la variable que guarda el tiempo de inicio
                switch (state.sortingMethod) {
                    case 1:
                        startTime = System.currentTimeMillis();
                        state.isSorted = Sorter.mergeSort(state.vector, state.mergeSortState, state.maxTime);
                        break;
                    case 2:
                        startTime = System.currentTimeMillis();
                        state.isSorted = Sorter.quickSort(state.vector, state.quickSortState, state.maxTime);
                        break;
                    case 3:
                        startTime = System.currentTimeMillis();
                        state.isSorted = Sorter.heapSort(state.vector, state.heapSortState, state.maxTime);
                        break;
                    default:
                        System.out.println("Opcion fuera del menú: nunca deberás pasar por aqui");
                        break;
                }
                long endTime = System.currentTimeMillis(); // Se obtiene el tiempo en el que se termina el ordenamiento
                state.elapsedTime +=  (endTime - startTime) / 1000.0; // Se suma el tiempo tardado al tiempo total
                if (!state.isSorted) {
                    // Se deja saber el estado al cliente
                    System.out.println("Trabajador " + workerId + " - Tiempo excedido, enviando al Trabajador " + (workerId + 1) % 2);
                    System.out.println("=====================================================");
                    Socket clientSocket = new Socket(clientHost, clientPort);
                    ObjectOutputStream outClient = new ObjectOutputStream(clientSocket.getOutputStream());
                    outClient.writeObject("Trabajador " + workerId + " - Tiempo excedido, enviando al Trabajador " + (workerId + 1) % 2);
                    outClient.flush();
                    clientSocket.close();

                    // Se envía el vector al siguiente worker
                    Socket nextWorkerSocket = new Socket(nextWorkerHost, nextWorkerPort);
                    ObjectOutputStream outNextWorker = new ObjectOutputStream(nextWorkerSocket.getOutputStream());
                    outNextWorker.writeObject(this.state);
                    outNextWorker.flush();
                    nextWorkerSocket.close();
                } else {
                    // Se deja saber el estado al Cliente
                    System.out.println("Trabajador " + workerId + " - Completó el ordenamiento");

                    // Se envía el resultado al cliente
                    SortingResult sortingResult = new SortingResult(state.vector, workerId, (float) ((endTime - startTime) / 1000.0), state.elapsedTime);
                    Socket clientSocket = new Socket(clientHost, clientPort);
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    out.writeObject(sortingResult);
                    out.flush();
                    System.out.println("=====================================================");
                }

            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Clase que representa el resultado del ordenamiento
class SortingResult implements Serializable {
    int[] vector;
    int workerId;
    double workerLasted;
    double totalLasted;

    public SortingResult(int[] vector, int workerId, float lasted, double totalLasted) {
        this.vector = vector;
        this.workerId = workerId;
        this.workerLasted = lasted;
        this.totalLasted = totalLasted;
    }
}

// Clase que representa el estado del ordenamiento
class SortingState implements Serializable {
    int[] vector;
    int sortingMethod;
    float maxTime;
    double elapsedTime;
    boolean isSorted;
    Sorter.MergeSortState mergeSortState;
    Sorter.HeapSortState heapSortState;
    Sorter.QuickSortState quickSortState;

    public SortingState(int[] vector, int sortingMethod, float maxTime, double elapsedTime) {
        this.vector = vector;
        this.sortingMethod = sortingMethod;
        this.maxTime = maxTime;
        this.elapsedTime = elapsedTime;
        this.isSorted = false;
        mergeSortState = new Sorter.MergeSortState(this.vector.length);
        heapSortState = new Sorter.HeapSortState(this.vector.length);
        quickSortState = new Sorter.QuickSortState(this.vector.length);
    }

}
