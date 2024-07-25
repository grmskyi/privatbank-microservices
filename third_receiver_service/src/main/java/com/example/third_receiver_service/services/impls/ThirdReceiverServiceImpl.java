package com.example.third_receiver_service.services.impls;


import com.example.third_receiver_service.dtos.ClientDataDTO;
import com.example.third_receiver_service.dtos.SavedContactsDTO;
import com.example.third_receiver_service.services.ThirdReceiverService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Setter
@Service
@RequiredArgsConstructor
public class ThirdReceiverServiceImpl implements ThirdReceiverService {

    @Value("${rabbitmq.queue.producer}")
    private String queueForProducer;

    private final RabbitTemplate rabbitTemplate;

    /**
     * Processes and sends updated client data to the designated queue for further processing.
     * This method mocks additional client details, including contacts and card numbers, before sending the updated
     * data to another queue. The primary purpose is to simulate the enrichment of client data as it passes through
     * various service layers in a microservices architecture.
     *
     * @param clientDataDTO the original client data received from previous services, encapsulated in {@link ClientDataDTO}.
     */
    @Override
    public void mockAndSendUpdatedClientDataTofFurthReceiverService(ClientDataDTO clientDataDTO) {
        var mockedClientData = mockingCustomerContactsAndCardNumbers(clientDataDTO);
        rabbitTemplate.convertAndSend(queueForProducer, mockedClientData);
        log.info("Updated client data sent to the queue {}: {}", queueForProducer, mockedClientData);
    }

    /**
     * Creates a mocked version of {@link ClientDataDTO} with additional details such as contacts and card numbers,
     * simulating a scenario where more client details are filled in as the data progresses through the system.
     *
     * @param clientDataDTO the original client data to be enhanced.
     * @return a {@link ClientDataDTO} with additional mocked details.
     */
    private ClientDataDTO mockingCustomerContactsAndCardNumbers(ClientDataDTO clientDataDTO) {
        return ClientDataDTO.builder()
                .clientId(clientDataDTO.getClientId())
                .firstName(clientDataDTO.getFirstName())
                .lastName(clientDataDTO.getLastName())
                .email(clientDataDTO.getEmail())
                .address(clientDataDTO.getAddress())
                .phoneNumber(clientDataDTO.getPhoneNumber())
                .savedContacts(List.of(
                        SavedContactsDTO
                                .builder()
                                .contactNumber("555 888 333 444")
                                .contactName("Valeria")
                                .build(),
                        SavedContactsDTO
                                .builder()
                                .contactNumber("666 777 444 333")
                                .contactName("Jack")
                                .build()
                ))
                .cardNumbers(List.of("5555 3333 6666 8888", "2222 1111 4444 5555"))
                .build();
    }
}