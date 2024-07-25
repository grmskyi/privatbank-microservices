package com.example.data_storage_service.configs;

import com.example.data_storage_service.dtos.ClientDataDTO;
import com.example.data_storage_service.mappers.ClientDataMapper;
import com.example.data_storage_service.models.ClientData;
import com.example.data_storage_service.services.DataStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitMQDataStorageConfiguration {

    @Value("${rabbitmq.queues.listener}")
    private String queueForListener;

    private final ClientDataMapper clientDataMapper;

    private final DataStorageService dataStorageService;

    @Bean
    public Queue queue() {
        return new Queue(queueForListener, false);
    }

    /**
     * Consumes messages from the specified RabbitMQ queue, converts them from {@link ClientDataDTO} to {@link ClientData},
     * logs the converted data, and then saves it to the database using {@link DataStorageService}.
     * This method is triggered by messages arriving in the RabbitMQ queue designated for client data processing.
     *
     * @param clientDataDTO the data transfer object (DTO) containing client information, received from the queue.
     *                      This DTO is mapped to the {@link ClientData} entity for persistence.
     */
    @RabbitListener(queues = "${rabbitmq.queues.listener}")
    public void listen(ClientDataDTO clientDataDTO) {
        log.info("Message read from furth queue: {}", clientDataDTO);
        ClientData clientData = clientDataMapper.toClientData(clientDataDTO);
        dataStorageService.saveClientData(clientData);
    }
}