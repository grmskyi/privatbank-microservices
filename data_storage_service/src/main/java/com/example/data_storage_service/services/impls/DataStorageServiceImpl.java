package com.example.data_storage_service.services.impls;

import com.example.data_storage_service.models.ClientData;
import com.example.data_storage_service.repositories.DataStorageRepository;
import com.example.data_storage_service.services.DataStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataStorageServiceImpl implements DataStorageService {

    private final DataStorageRepository dataStorageRepository;

    @Override
    public void saveClientData(ClientData clientData) {
        dataStorageRepository.save(clientData);
    }
}