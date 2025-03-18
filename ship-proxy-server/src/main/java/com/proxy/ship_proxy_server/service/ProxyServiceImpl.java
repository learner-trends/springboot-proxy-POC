package com.proxy.ship_proxy_server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProxyServiceImpl implements ProxyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyService.class);

    public ResponseEntity<String> processRequest(String request) {
        LOGGER.info("Received request from ship proxy: {}", request);
        // Forward request to the internet (mocked for now)
        String response = "Processed: " + request;
        LOGGER.info("Sending response: {}", response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
