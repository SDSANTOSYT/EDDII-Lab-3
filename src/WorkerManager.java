import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class WorkerManager implements Runnable {

    private Socket clientSocket;
    private int workerId;
    private String nextWorkerHost;
    private int nextWorkerPort;
    private int maxTime;

    public WorkerManager(Socket clientSocket, int workerId, String nextWorkerHost, int nextWorkerPort, int maxTime) {
        this.clientSocket = clientSocket;
        this.workerId = workerId;
        this.nextWorkerHost = nextWorkerHost;
        this.nextWorkerPort = nextWorkerPort;
        this.maxTime = maxTime;
    }


    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            int[] array = (int[]) in.readObject();
            boolean isSorted = in.readBoolean();
            clientSocket = (Socket) in.readObject();

            System.out.println("Trabajador " + workerId + " - Tiempo excedido, enviando a siguiente trabajador");

            if (!isSorted) {
                Thread sortingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sort(array);
                    }
                });
                sortingThread.start();
                sortingThread.join(maxTime * 1000L);

                if (sortingThread.isAlive()) {
                    sortingThread.interrupt();
                    System.out.println("Trabajador " + workerId + " - Tiempo excedido, enviando a siguiente trabajador");

                    Socket nextWorkerSocket = new Socket(nextWorkerHost, nextWorkerPort);
                    ObjectOutputStream outNextWorker = new ObjectOutputStream(nextWorkerSocket.getOutputStream());
                    outNextWorker.writeObject(clientSocket);
                    outNextWorker.writeInt(maxTime);
                    outNextWorker.writeObject(array);
                    outNextWorker.writeBoolean(false);
                    outNextWorker.flush();

                    nextWorkerSocket.close();
                } else {
                    System.out.println("Trabajador " + workerId + " - Completó el ordenamiento");
                    SortingResult sortingResult = new SortingResult(array,workerId,maxTime);
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    out.writeObject(sortingResult);
                    out.flush();
                }

            }
        clientSocket.close();
        } catch (Exception e) {

        }
    }

    public void sort(int[] array) {
        //añadir metodos de ordenamiento
    }
}

class SortingResult implements Serializable{
    int[] vector;
    int workerId;
    int lasted;

    public SortingResult(int[] vector, int workerId, int lasted) {
        this.vector = vector;
        this.workerId = workerId;
        this.lasted = lasted;
    }
}
