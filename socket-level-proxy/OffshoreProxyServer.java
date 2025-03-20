import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class OffshoreProxyServer {

    private static final int SERVER_PORT = 9090;

    public static void main(String[] args) {
        new OffshoreProxyServer().startServer();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Offshore Proxy Server started on port " + SERVER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String requestLine = in.readLine();
            if (requestLine == null) return;

            System.out.println("Offshore Proxy Received: " + requestLine);

            // Read and ignore headers for now
            String line;
            while (!(line = in.readLine()).isEmpty()) {}

            // Mocked response
            String response = "HTTP/1.1 200 OK\r\nContent-Length: 20\r\n\r\nMock Response Data";
            out.write(response);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
