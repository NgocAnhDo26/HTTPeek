package src.main.java.httpeek;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;

    final String PUBLIC_FOLDER = "./public";
    final String DEFAULT_RESOURCE = "/index.html";

    public ClientHandler(Socket clientSocket) {
        this.socket = clientSocket;
    }

    @Override
    public void run() {
        System.out.println(socket.getInetAddress() + " connected to the server!");

        try {
            InputStream inputStream = socket.getInputStream();
            BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());

            List<String> headers = readHeaders(inputStream);

            String[] requestComponents = headers.get(0).split(" ");
            //String requestMethod = requestComponents[0];
            //String resourceLocation = requestComponents[1];
            String requestProtocol = requestComponents[2];

            String resourceUri = PUBLIC_FOLDER;
            if (requestComponents[1].equals("/")) {
                resourceUri += DEFAULT_RESOURCE;
            } else {
                resourceUri += requestComponents[1];
            }

            // Find the resource, read and send it
            try {
                File file = new File(resourceUri);
                FileInputStream resource = new FileInputStream(file);
                String fileExtension = Utils.getFileExtension(file.getName());
                String mimeType = Utils.getMimeType(fileExtension);
                System.out.println(fileExtension + " " + mimeType);

                writer.write(Utils.toBytes(requestComponents[2] + " 200 OK\n"));
                writer.write(Utils.toBytes("content-type: " + mimeType + "; charset=utf-8\n"));
                writer.write(Utils.toBytes("content-length: " + file.length() + "\n"));
                writer.write("\n".getBytes());

                int i;
                do {
                    i = resource.read();
                    writer.write(i);
                } while (i != -1);

                resource.close();
            } catch (FileNotFoundException ex) {
                System.out.println("Resource " + resourceUri + " not found!");
                writer.write(Utils.toBytes(requestProtocol + " 404 Not found\n"));
                writer.write("\n".getBytes());
            } catch (IOException ex) {
                System.out.println("An I/O error occured!");
                System.out.println(ex.getMessage());
                writer.write(Utils.toBytes(requestProtocol + " 500 Internal server error\n"));
                writer.write("\n".getBytes());
            } finally {
                System.out.println("Request handled: " + headers.get(0) + " from " + socket.getInetAddress());
                writer.flush();
                socket.close();
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static String readLine(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int b;
        while ((b = input.read()) != -1) {
            if (b == '\n') {
                break;
            }

            if (b != '\r') {
                baos.write(b);
            }
        }

        if (b == -1 && baos.size() == 0) {
            return null;
        }

        return baos.toString();
    }

    private static List<String> readHeaders(InputStream input) throws IOException {
        List<String> headers = new ArrayList<>();

        String line;
        while (!(line = readLine(input)).isEmpty()) {
            headers.add(line);
        }

        return headers;
    }
}
