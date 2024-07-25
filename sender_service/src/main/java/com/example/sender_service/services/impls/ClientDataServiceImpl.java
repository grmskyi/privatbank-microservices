package com.example.sender_service.services.impls;

import com.example.sender_service.services.ClientDataService;
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
public class ClientDataServiceImpl implements ClientDataService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchanges.value}")
    private String exchangeName;

    /**
     * Sends the client identifier to a specified RabbitMQ exchange using a predefined routing key.
     * This method uses the RabbitTemplate to send the client ID to the queue configured at the exchange specified in
     * the application properties under the routing key "first.key".
     *
     * @param clientId the unique identifier of the client to be sent to the queue.
     */
    @Override
    public void sendClientData(String clientId) {
        rabbitTemplate.convertAndSend(exchangeName, "first.key", clientId);
        log.info("Sending client data by id {}", clientId);
    }
}