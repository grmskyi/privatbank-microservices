package com.example.data_storage_service.services;

import com.example.data_storage_service.models.ClientData;

public interface DataStorageService {
    void saveClientData(ClientData clientData);
}