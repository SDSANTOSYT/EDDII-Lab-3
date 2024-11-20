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
    private int maxTime;

    public WorkerManager(Socket socket, int workerId, String nextWorkerHost, int nextWorkerPort) {
        this.socket = socket;
        this.workerId = workerId;
        this.nextWorkerHost = nextWorkerHost;
        this.nextWorkerPort = nextWorkerPort;
    }


    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            int[] array = (int[]) in.readObject();
            this.maxTime = in.readInt();
            boolean isSorted = in.readBoolean();

            System.out.println("Trabajador " + workerId + " - Comenzará a ordenar");

            if (!isSorted) {
                long startTime = System.currentTimeMillis();
                Thread sortingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sort(array);
                    }
                });
                sortingThread.start();
                sortingThread.join(this.maxTime * 1000L);

                if (sortingThread.isAlive()) {
                    System.out.println("No se terminó de ordenar");
                    sortingThread.interrupt();
                    System.out.println("Trabajador " + workerId + " - Tiempo excedido, enviando a siguiente trabajador");

                    Socket nextWorkerSocket = new Socket(nextWorkerHost, nextWorkerPort);
                    ObjectOutputStream outNextWorker = new ObjectOutputStream(nextWorkerSocket.getOutputStream());
                    outNextWorker.writeObject(array);
                    outNextWorker.writeInt(this.maxTime);
                    outNextWorker.writeBoolean(false);
                    outNextWorker.flush();

                    nextWorkerSocket.close();
                } else {
                    long endTime = System.currentTimeMillis();
                    System.out.println("Trabajador " + workerId + " - Completó el ordenamiento");

                    SortingResult sortingResult = new SortingResult(array, workerId, (int) ((endTime - startTime) / 1000));
                    Socket clientSocket = new Socket("localhost", 5002);
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    out.writeObject(sortingResult);
                    out.flush();
                }

            }
            socket.close();
        } catch (Exception e) {
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
    int lasted;

    public SortingResult(int[] vector, int workerId, int lasted) {
        this.vector = vector;
        this.workerId = workerId;
        this.lasted = lasted;
    }
}
