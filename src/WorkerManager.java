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
    private float maxTime;
    private final String clientHost="localhost";
    private final int clientPort=5002;

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
            int op = in.readInt();
            System.out.println(op);
            int[] array = (int[]) in.readObject();
            this.maxTime = in.readFloat();
            System.out.println(maxTime);
            boolean isSorted = in.readBoolean();

            System.out.println("Trabajador " + workerId + " - Comenzará a ordenar");

            if (!isSorted) {
                Thread sortingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        switch (op) {
                            case 1:
                                Sorter.mergeSorts(array, array.length);
                                break;
                            case 2:
                                Sorter.quickSort(array);
                                break;
                            case 3:
                                Sorter.heapsort(array);
                                break;
                            default:
                                System.out.println("Opcion fuera del menú: nunca deberás pasar por aqui");
                                break;
                        }
                    }
                });
                long startTime = System.currentTimeMillis();
                sortingThread.start();
                sortingThread.join((long) (this.maxTime * 1000));

                if (sortingThread.isAlive()) {
                    sortingThread.interrupt();
                    System.out.println("Trabajador " + workerId + " - Tiempo excedido, enviando a siguiente trabajador");
                    Socket clientSocket = new Socket(clientHost, clientPort);
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    out.writeObject("Trabajador " + workerId + " - Tiempo excedido, enviando a siguiente trabajador");
                    out.flush();
                    clientSocket.close();

                    Socket nextWorkerSocket = new Socket(nextWorkerHost, nextWorkerPort);
                    ObjectOutputStream outNextWorker = new ObjectOutputStream(nextWorkerSocket.getOutputStream());
                    outNextWorker.writeInt(op);
                    outNextWorker.writeObject(array);
                    outNextWorker.writeFloat(this.maxTime);
                    outNextWorker.writeBoolean(false);
                    outNextWorker.flush();

                    nextWorkerSocket.close();
                } else {
                    long endTime = System.currentTimeMillis();
                    System.out.println("Trabajador " + workerId + " - Completó el ordenamiento");

                    SortingResult sortingResult = new SortingResult(array, workerId, (float) ((endTime - startTime) / 1000));
                    Socket clientSocket = new Socket(clientHost, clientPort);
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
    float lasted;

    public SortingResult(int[] vector, int workerId, float lasted) {
        this.vector = vector;
        this.workerId = workerId;
        this.lasted = lasted;
    }
}
