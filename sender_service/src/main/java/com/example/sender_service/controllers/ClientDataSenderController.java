package com.example.sender_service.controllers;

import com.example.sender_service.services.ClientDataService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class ClientDataSenderController {


    private final ClientDataService clientDataService;

    @PostMapping("/send")
    public ResponseEntity<String> sendData(@RequestBody String clientId) {
        clientDataService.sendClientData(clientId);
        return new ResponseEntity<>(clientId, HttpStatus.CREATED);
    }
}