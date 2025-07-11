package src.main.java.httpeek;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Program {
    public static void main(String[] args) {
        try (ServerSocket socket = new ServerSocket(7749)) {
            System.out.println("Waiting for connection...");
            while (true) {
                Socket client = socket.accept();

                // A client connected
                ClientHandler clientHandler = new ClientHandler(client);
                Thread handler = new Thread(clientHandler);
                handler.start();
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}