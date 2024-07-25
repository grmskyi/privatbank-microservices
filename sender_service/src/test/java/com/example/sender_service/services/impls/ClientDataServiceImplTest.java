package com.example.sender_service.services.impls;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class ClientDataServiceImplTest {
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ClientDataServiceImpl clientDataService;

    @BeforeEach
    void setUp() {
        clientDataService = new ClientDataServiceImpl(rabbitTemplate);
        clientDataService.setExchangeName("myExchange");
    }

    @Test
    void sendClientData_sendsDataToRabbitMQ() {
        String clientId = "12345";

        clientDataService.sendClientData(clientId);

        verify(rabbitTemplate).convertAndSend("myExchange", "first.key", clientId);
        System.out.println("Verified that convertAndSend was called with exchange: myExchange" +
                ", routingKey: first.key, and clientId: " + clientId);
    }
}