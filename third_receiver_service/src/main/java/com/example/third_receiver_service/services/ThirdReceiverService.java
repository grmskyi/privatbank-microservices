package com.example.third_receiver_service.services;


import com.example.third_receiver_service.dtos.ClientDataDTO;

public interface ThirdReceiverService {
    void mockAndSendUpdatedClientDataTofFurthReceiverService(ClientDataDTO clientDataDTO);
}