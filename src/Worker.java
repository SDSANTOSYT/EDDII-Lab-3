import java.net.ServerSocket;
import java.net.Socket;

public class Worker {


    public static void main(String[] args) {
        try {
            // se crea el socket del servidor
            ServerSocket server = new ServerSocket(5000);
            System.out.println("Worker#0 Iniciado");
            while (true) {
                // se esperan las conexiones ya sea del cliente o del otro worker
                Socket socket = server.accept();
                new Thread(new WorkerManager(socket, 0, "localhost", 5001)).start();
            }
        } catch (Exception e) {
            System.out.println("falló en algo");
            e.printStackTrace();
        }

    }
}