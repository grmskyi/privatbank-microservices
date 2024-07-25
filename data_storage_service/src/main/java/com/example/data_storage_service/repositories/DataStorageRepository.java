package com.example.data_storage_service.repositories;

import com.example.data_storage_service.models.ClientData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataStorageRepository extends JpaRepository<ClientData, Long> {
}