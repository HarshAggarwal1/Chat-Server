package client;

public class ClientMain {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java client.ClientMain <host> <port>" );
            return;
        }

        String host = args[0];
        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number: " + args[1]);
            return;
        }

        ChatClient client = new ChatClient(host, port);
        client.start();
    }
}
