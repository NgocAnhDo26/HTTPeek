package src.main.java.httpeek;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class TestClient {
    public static void main(String[] args) {
        try {
            try (Socket client = new Socket("localhost", 7749)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                System.out.println("Connected to the server: " + client.getInetAddress());

                Scanner scanner = new Scanner(System.in);
                String messageToSend;
                do {
                    messageToSend = scanner.nextLine();
                    writer.write(messageToSend + "\n");
                    writer.flush();
                    String response = reader.readLine();
                    System.out.println(response);
                } while (!messageToSend.equals("stop"));

                System.out.println("Close connection with the server...");
                scanner.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
