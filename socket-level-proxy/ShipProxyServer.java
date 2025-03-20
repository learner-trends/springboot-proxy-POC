import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ShipProxyServer {

    private static final int PROXY_PORT = 8080;

    public static void main(String[] args) {
        new ShipProxyServer().startProxy();
    }

    public void startProxy() {
        try (ServerSocket serverSocket = new ServerSocket(PROXY_PORT)) {
            System.out.println("Ship Proxy started on port " + PROXY_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClientConnection(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientConnection(Socket clientSocket) {
        try {
            InputStream clientInput = clientSocket.getInputStream();
            OutputStream clientOutput = clientSocket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientInput));

            String requestLine = reader.readLine();
            if (requestLine == null) return;

            System.out.println("Received request: " + requestLine);

            if (requestLine.startsWith("CONNECT")) {
                handleConnectToTarget(requestLine, clientSocket, reader, clientOutput);
            } else {
                forwardHttpRequestToOffshoreProxy(requestLine, reader, clientOutput);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleConnectToTarget(String connectRequest, Socket clientSocket,
                                       BufferedReader clientReader, OutputStream clientOutput) {
        try {
            String[] parts = connectRequest.split(" ");
            String hostPort = parts[1];
            String[] hp = hostPort.split(":");
            String host = hp[0];
            int port = Integer.parseInt(hp[1]);

            Socket targetSocket = new Socket(host, port);
            clientOutput.write("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes());
            clientOutput.flush();

            // Relay data between client and target
            Thread clientToTarget = new Thread(() -> relayData(clientSocket, targetSocket));
            Thread targetToClient = new Thread(() -> relayData(targetSocket, clientSocket));

            clientToTarget.start();
            targetToClient.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void forwardHttpRequestToOffshoreProxy(String requestLine, BufferedReader clientReader,
                                                   OutputStream clientOutput) {
        try (Socket offshoreSocket = new Socket("localhost", 9090);
             OutputStream offshoreOutput = offshoreSocket.getOutputStream();
             InputStream offshoreInput = offshoreSocket.getInputStream();
             BufferedWriter offshoreWriter = new BufferedWriter(new OutputStreamWriter(offshoreOutput));
             BufferedReader offshoreReader = new BufferedReader(new InputStreamReader(offshoreInput))) {

            offshoreWriter.write(requestLine + "\r\n");
            String header;
            while (!(header = clientReader.readLine()).isEmpty()) {
                offshoreWriter.write(header + "\r\n");
            }
            offshoreWriter.write("\r\n");
            offshoreWriter.flush();

            String responseLine;
            while ((responseLine = offshoreReader.readLine()) != null) {
                clientOutput.write((responseLine + "\r\n").getBytes());
                clientOutput.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void relayData(Socket inputSocket, Socket outputSocket) {
        try (InputStream in = inputSocket.getInputStream();
             OutputStream out = outputSocket.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                out.flush();
            }

        } catch (IOException e) {
            // Connection might be closed, which is expected
        }
    }
}
