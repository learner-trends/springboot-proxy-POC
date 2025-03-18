package com.proxy.ship_proxy_server.controller;

import com.proxy.ship_proxy_server.service.ProxyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    private final ProxyService proxyService;

    public ProxyController(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @PostMapping("/request")
    public ResponseEntity<String> handleRequest(@RequestBody String request) {
        return proxyService.processRequest(request);
    }
}
