package com.example.second_receiver_service.services.impls;


import com.example.second_receiver_service.dtos.ClientDataDTO;
import com.example.second_receiver_service.services.SecondReceiverService;
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
public class SecondReceiverServiceImpl implements SecondReceiverService {

    @Value("${rabbitmq.queue.producer}")
    private String queueForProducer;

    private final RabbitTemplate rabbitTemplate;

    /**
     * Processes the provided {@link ClientDataDTO} by mocking additional client details, such as email, phone number,
     * and address. This method enhances the data integrity and completeness before forwarding it to another service
     * or queue. After mocking the necessary details, it sends the updated {@link ClientDataDTO} to a specified RabbitMQ
     * queue for further processing or storage.
     *
     * @param clientDataDTO the initial client data received, which lacks some details like email, address, and phone number.
     *                      This object is updated with mocked data to enhance its completeness.
     */
    @Override
    public void mockAndSendUpdatedClientDataToThirdReceiverService(ClientDataDTO clientDataDTO) {
        var mockedClientData = mockingTheClientsEmailNumberAndAddress(clientDataDTO);
        rabbitTemplate.convertAndSend(queueForProducer, mockedClientData);
        log.info("Updated client data sent to the queue {}: {}", queueForProducer, mockedClientData);
    }

    /**
     * Creates a more complete version of {@link ClientDataDTO} by adding mocked values for email, address, and phone number.
     * This method is used internally to prepare data before sending it through the messaging system.
     *
     * @param clientDataDTO the client data to be enhanced with additional mocked details.
     * @return a new instance of {@link ClientDataDTO} enriched with mocked email, phone number, and address.
     */
    private ClientDataDTO mockingTheClientsEmailNumberAndAddress(ClientDataDTO clientDataDTO) {
        return ClientDataDTO.builder()
                .clientId(clientDataDTO.getClientId())
                .firstName(clientDataDTO.getFirstName())
                .lastName(clientDataDTO.getLastName())
                .email("john.doe@example.com")
                .address("123 Main Street")
                .phoneNumber("555-555-5555")
                .build();
    }
}