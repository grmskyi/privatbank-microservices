package com.example.first_receiver_service.configs;

import com.example.first_receiver_service.services.FirstReceiverService;
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
public class RabbitMQFirstReceiverConfiguration {

    @Value("${rabbitmq.queues.listener}")
    private String queueForListener;

    private final FirstReceiverService firstReceiverService;

    @Bean
    public Queue queue() {
        return new Queue(queueForListener, false);
    }

    /**
     * Listens for messages on the configured RabbitMQ queue and processes each received message.
     * This method is triggered when a message is received on the 'firstQueue'. The method logs the received
     * message, which contains a client ID, and then calls a service method to process and potentially
     * modify the client data based on business logic, before sending it to another service or queue.
     *
     * @param clientId the client ID received from the message on the queue. This ID is used to fetch,
     *                 process, and forward client data through the {@link FirstReceiverService}.
     */
    @RabbitListener(queues = "${rabbitmq.queues.listener}")
    public void listen(String clientId) {
        log.info("Message read from first queue: {}", clientId);
        firstReceiverService.mockAndSendUpdatedClientDataToSecondReceiverService(clientId);
    }
}