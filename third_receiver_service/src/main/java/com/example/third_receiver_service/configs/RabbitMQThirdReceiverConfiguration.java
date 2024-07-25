package com.example.third_receiver_service.configs;


import com.example.third_receiver_service.dtos.ClientDataDTO;
import com.example.third_receiver_service.services.ThirdReceiverService;
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
public class RabbitMQThirdReceiverConfiguration {

    @Value("${rabbitmq.queues.listener}")
    private String queueForListener;

    private final ThirdReceiverService thirdReceiverService;

    @Bean
    public Queue queue() {
        return new Queue(queueForListener, false);
    }

    /**
     * Defines a RabbitMQ listener method that triggers upon receiving messages from the 'thirdQueue'.
     * The method handles incoming {@link ClientDataDTO} objects, logs the received message, and passes the data
     * to the {@link ThirdReceiverService} for further processing and eventual forwarding to another service or queue.
     *
     * @param clientDataDTO the client data transferred from the previous queue, encapsulated in {@link ClientDataDTO}.
     */
    @RabbitListener(queues = "${rabbitmq.queues.listener}")
    public void listen(ClientDataDTO clientDataDTO) {
        log.info("Message read from third queue: {}", clientDataDTO);
        thirdReceiverService.mockAndSendUpdatedClientDataTofFurthReceiverService(clientDataDTO);
    }
}