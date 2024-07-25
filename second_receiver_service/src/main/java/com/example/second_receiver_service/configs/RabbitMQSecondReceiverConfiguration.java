package com.example.second_receiver_service.configs;

import com.example.second_receiver_service.dtos.ClientDataDTO;
import com.example.second_receiver_service.services.SecondReceiverService;
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
public class RabbitMQSecondReceiverConfiguration {

    @Value("${rabbitmq.queues.listener}")
    private String queueForListener;

    private final SecondReceiverService secondReceiverService;

    @Bean
    public Queue queue() {
        return new Queue(queueForListener, false);
    }

    /**
     * Listens for messages on the configured RabbitMQ queue, processes received {@link ClientDataDTO} objects by
     * forwarding them to the {@link SecondReceiverService}. This method is triggered whenever a message is available
     * in the 'secondQueue'. Upon receiving a message, it logs the received data and calls a service method to further
     * handle the data, potentially involving transformations or additional business logic before forwarding to another
     * service or queue.
     *
     * @param clientDataDTO the client data transferred from the queue, encapsulated in a {@link ClientDataDTO} object.
     *                      This data is used to perform further processing by the {@link SecondReceiverService}.
     */
    @RabbitListener(queues = "${rabbitmq.queues.listener}")
    public void listen(ClientDataDTO clientDataDTO) {
        log.info("Message read from second queue: {}", clientDataDTO);
        secondReceiverService.mockAndSendUpdatedClientDataToThirdReceiverService(clientDataDTO);
    }
}