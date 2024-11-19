import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Worker {


    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(5000);
            while (true) {
                Socket socket = server.accept();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                Socket client = (Socket) in.readObject();
                int maxTime = (int) in.readInt();
                new Thread(new WorkerManager(client, 0, "localhost", 5001, maxTime)).start();
            }
        } catch (Exception e) {

        }
    }
}