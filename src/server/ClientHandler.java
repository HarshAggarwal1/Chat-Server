package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ChatServer server;
    private BufferedReader in;
    private PrintWriter out;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private String username;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void sendMessage(String message) {
        if (this.closed.get()) {
            return;
        }
        try {
            out.println(message);
        } catch (Exception e) {
            this.closed.set(true);
            this.server.removeClient(this);
        }
    }

    @Override
    public void run() {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);

            out.print("Enter your username: ");
            out.flush();
            this.username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                out.println("Invalid username. Disconnecting...");
                closed.set(true);
                return;
            }
            out.println("Welcome " + username + "! You can start chatting now.");
            String message;

            while (!closed.get() && (message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    out.println("Goodbye " + username + "!");
                    closed.set(true);
                    break;
                }
                server.broadcastMessage(username + ": " + message);
            }
        }
        catch (Exception e) {
            closed.set(true);
        }
        finally {
            try {
                socket.close();
            } catch (Exception e) {
                Logger.getLogger(ClientHandler.class.getName()).log(java.util.logging.Level.SEVERE, "Error closing socket", e);
            }
            server.removeClient(this);
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
