import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        try {

            // Se lee el vector desde el txt
            int[] array = readArray("src\\vector.txt");
            // Se lee el tiempo maximo para la ejecución del ordenamiento
            System.out.println("Ingresa el tiempo maximo para ordenar el vector");
            float maxTime = input.nextFloat();
            int op=0;
            
            while (op>3 || op<1) {
                try {
                    System.out.println("Ingrese como quiere organizar el vector\n MergeSort (1), QuickSort (2) HeapSort (3)");
                        op=input.nextInt();
                    
                } catch (Exception e) {
                    op=0;
                }
                
            }
            


            // Se crea la conexión con el socket del worker#0
            Socket socket = new Socket("localhost", 5000);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            out.writeInt(op);//Se envia la opción
            out.writeObject(array); // se envia el vector
            out.writeFloat(maxTime); // se envia el tiempo limite para resolver el problema
            out.writeBoolean(false); // se envia un booleano que indica el estado del vector
            out.flush();

            System.out.println("Vector enviado al Trabajador #0");

            socket.close();

            // En esta fase el cliente pasa a ser un servidor que espera las respuestas de los workers
            ServerSocket server = new ServerSocket(5002);
            while (true) {
                Socket socket1 = server.accept(); // Espera a que los workers se conecten a el
                ObjectInputStream in = new ObjectInputStream(socket1.getInputStream());
                Object object = in.readObject(); // Lee los objetos enviados por los workers
                if (object instanceof String) {
                    // Si el objeto es un mensaje, lo imprime en consola
                    System.out.println(object);
                } else if (object instanceof SortingResult) {
                    // Si el objeto es el resultado del ordenamiento, se guarda el vector en un archivo y se muestra el tiempo de ejecución
                    SortingResult results = (SortingResult) object;
                    writeArray("src/vectorOrdenado.txt", results.vector);
                    System.out.println("El vector fue ordenado por el trabajador #" + results.workerId + " en " + results.lasted + " segundos");
                    break;
                }
            }
            server.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Función que devuelve un vector desde un archivo
    public static int[] readArray(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            ArrayList<Integer> array = new ArrayList<>();
            String linea;
            while ((linea = br.readLine()) != null) {
                // Convertir la línea a número entero y agregarla a la lista
                array.add(Integer.parseInt(linea));
            }
            br.close();
            return array.stream().mapToInt(i -> i).toArray();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Procedimiento que escribe un vector en un archivo
    public static void writeArray(String filename, int[] array) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (int element : array) {
                bw.write(element + "\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
