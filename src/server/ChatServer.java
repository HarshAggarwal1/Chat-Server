package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServer {
    private final int port;
    private final ExecutorService executor;
    private final CopyOnWriteArrayList<ClientHandler> clients;

    public ChatServer(int port) {
        this.port = port;
        this.executor = Executors.newCachedThreadPool();
        this.clients = new CopyOnWriteArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.INFO, "Server started");
            while (true) {
                Socket socket = serverSocket.accept();
                Logger.getLogger(ServerMain.class.getName()).log(Level.INFO, "Client connected: " + socket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(socket, this);
                this.clients.add(clientHandler);
                this.executor.submit(clientHandler);
            }
        }
        catch (IOException error) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, "The server could not be started.", error);
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        this.clients.remove(clientHandler);
        Logger.getLogger(ServerMain.class.getName()).log(
                Level.INFO,
                "Client disconnected: " + clientHandler.getSocket().getInetAddress()
        + " | Remaining clients: " + this.clients.size());
    }

    public void broadcastMessage(String message) {
        for (ClientHandler client : this.clients) {
            client.sendMessage(message);
        }

        Logger.getLogger(ServerMain.class.getName()).log(Level.INFO, "Broadcasting message: " + message);
    }
}
