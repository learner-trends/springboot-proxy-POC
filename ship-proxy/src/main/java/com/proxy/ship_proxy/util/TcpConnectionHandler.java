package com.proxy.ship_proxy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

@Component
public class TcpConnectionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpConnectionHandler.class);
    private static final String OFFSHORE_HOST = "localhost";
    private static final int OFFSHORE_PORT = 9090;

    public void startConnection(BlockingQueue<String> requestQueue) {
        new Thread(() -> {
            try (Socket socket = new Socket(OFFSHORE_HOST, OFFSHORE_PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                LOGGER.info("Established persistent TCP connection with offshore server at {}:{}", OFFSHORE_HOST, OFFSHORE_PORT);

                while (true) {
                    String request = requestQueue.take();
                    LOGGER.info("Processing request: {}", request);
                    out.println(request);
                    String response = in.readLine();
                    LOGGER.info("Received response: {}", response);
                }
            } catch (Exception e) {
                LOGGER.error("TCP connection error: ", e);
            }
        }, "TCP-Connection-Thread").start();
    }
}
