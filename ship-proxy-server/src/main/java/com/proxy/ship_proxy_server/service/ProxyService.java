package com.proxy.ship_proxy_server.service;

import org.springframework.http.ResponseEntity;

public interface ProxyService {
    ResponseEntity<String> processRequest(String request);
}
