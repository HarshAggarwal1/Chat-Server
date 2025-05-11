package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private final String host;
    private final int port;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try (
                Socket socket = new Socket(host, port);
                BufferedReader in = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        ) {

            Thread thread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (Exception e) {
                    System.err.println("Error reading from server: " + e.getMessage());
                }
            });
            thread.setDaemon(true);
            thread.start();

            System.out.println("Connected to chat server. Type your messages below:");

            String message;
            while ((message = console.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                out.println(message);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            System.out.println("Disconnected from chat server.");
            System.out.println("Exiting chat client.");
        }
    }
}
