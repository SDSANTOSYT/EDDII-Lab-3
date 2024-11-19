import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Worker {

    public void startServer(){
        ServerSocket server = null;
        Socket socket = null;
        int port = 5000;

        try{
            server = new ServerSocket(port);
            System.out.println("Server Started");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {


    }
}
