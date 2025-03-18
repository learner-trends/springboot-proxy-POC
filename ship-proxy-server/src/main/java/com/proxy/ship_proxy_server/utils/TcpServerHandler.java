package com.proxy.ship_proxy_server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class TcpServerHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpServerHandler.class);
    private static final int SERVER_PORT = 9090;

    public TcpServerHandler() {
        startServer();
    }

    private void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
                LOGGER.info("Offshore Proxy Server started on port {}", SERVER_PORT);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(() -> handleClient(clientSocket)).start();
                }
            } catch (IOException e) {
                LOGGER.error("TCP Server error: ", e);
            }
        }, "TCP-Server-Thread").start();
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request;
            while ((request = in.readLine()) != null) {
                LOGGER.info("Received request: {}", request);
                String response = "Processed: " + request;
                out.println(response);
                LOGGER.info("Sent response: {}", response);
            }
        } catch (IOException e) {
            LOGGER.error("Client connection error: ", e);
        }
    }
}

