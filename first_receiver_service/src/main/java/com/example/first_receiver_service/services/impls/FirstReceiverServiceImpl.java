package com.example.first_receiver_service.services.impls;

import com.example.first_receiver_service.dtos.ClientDataDTO;
import com.example.first_receiver_service.services.FirstReceiverService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Setter
@Service
@RequiredArgsConstructor
public class FirstReceiverServiceImpl implements FirstReceiverService {

    @Value("${rabbitmq.queue.producer}")
    private String queueForProducer;

    private final RabbitTemplate rabbitTemplate;

    /**
     * Processes a client ID by generating mocked client data and then sends this data to a specified RabbitMQ queue.
     * This method first creates a mocked {@link ClientDataDTO} object using the provided client ID, then sends this
     * object to the designated producer queue. It logs the action of sending the client data, including the queue name
     * and the data itself, ensuring traceability and easy debugging.
     *
     * @param clientId the client ID for which the data is being mocked and sent. This ID is used to generate the mocked
     *                 data encapsulated in a {@link ClientDataDTO} object.
     */
    @Override
    public void mockAndSendUpdatedClientDataToSecondReceiverService(String clientId) {
        var clientDataDTO = mockingClientsInitials(clientId);
        rabbitTemplate.convertAndSend(queueForProducer, clientDataDTO);
        log.info("Updated client data sent to the queue {}: {}", queueForProducer, clientDataDTO);
    }

    /**
     * Generates a mocked {@link ClientDataDTO} object based on a given client ID. This method is intended to simulate
     * a scenario where client data is retrieved and populated, mimicking a real-world data fetch operation.
     *
     * @param clientId the client ID to create a mocked client data object for.
     * @return a {@link ClientDataDTO} containing mocked values for the client, including the provided client ID.
     */
    private ClientDataDTO mockingClientsInitials(String clientId) {
        return ClientDataDTO.builder()
                .clientId(clientId)
                .firstName("John")
                .lastName("Doe")
                .build();
    }
}