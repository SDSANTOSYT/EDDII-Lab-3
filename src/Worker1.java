import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Worker1 {


    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(5001);
            while (true) {
                Socket socket = server.accept();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                Socket client = (Socket) in.readObject();
                int maxTime = (int) in.readInt();
                new Thread(new WorkerManager(client, 1, "localhost", 5000, maxTime)).start();
            }
        } catch (Exception e) {

        }
    }

}
