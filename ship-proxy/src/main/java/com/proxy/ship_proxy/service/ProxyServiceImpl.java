package com.proxy.ship_proxy.service;

import com.proxy.ship_proxy.util.TcpConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class ProxyServiceImpl implements ProxyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyService.class);
    private static final BlockingQueue<String> REQUEST_QUEUE = new LinkedBlockingQueue<>();

    public ProxyServiceImpl(TcpConnectionHandler tcpHandler) {
        tcpHandler.startConnection(REQUEST_QUEUE);
    }

    @Override
    public ResponseEntity<String> processRequest(String request) {
        try {
            REQUEST_QUEUE.put(request);
            LOGGER.info("Request queued successfully: {}", request);
            return ResponseEntity.ok("Request queued successfully");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Failed to queue request: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to queue request");
        }
    }
}
