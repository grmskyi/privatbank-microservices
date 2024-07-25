package com.example.second_receiver_service.services;


import com.example.second_receiver_service.dtos.ClientDataDTO;

public interface SecondReceiverService {
    void mockAndSendUpdatedClientDataToThirdReceiverService(ClientDataDTO clientDataDTO);
}