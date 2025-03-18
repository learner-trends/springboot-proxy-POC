package com.proxy.ship_proxy.service;

import org.springframework.http.ResponseEntity;

public interface ProxyService {
    ResponseEntity<String> processRequest(String request);
}
