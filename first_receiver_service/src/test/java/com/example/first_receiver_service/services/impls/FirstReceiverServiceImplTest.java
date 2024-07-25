package com.example.first_receiver_service.services.impls;

import com.example.first_receiver_service.dtos.ClientDataDTO;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class FirstReceiverServiceImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private FirstReceiverServiceImpl firstReceiverService;


    @BeforeEach
    void setUp() {
        firstReceiverService = new FirstReceiverServiceImpl(rabbitTemplate);
        firstReceiverService.setQueueForProducer("testQueue");
    }

    @Test
    void mockAndSendUpdatedClientDataToSecondReceiverService_sendsCorrectData() {
        String clientId = "12345";
        ArgumentCaptor<ClientDataDTO> clientDataCaptor = ArgumentCaptor.forClass(ClientDataDTO.class);

        firstReceiverService.mockAndSendUpdatedClientDataToSecondReceiverService(clientId);

        verify(rabbitTemplate).convertAndSend(eq("testQueue"), clientDataCaptor.capture());
        ClientDataDTO sentData = clientDataCaptor.getValue();

        assertNotNull(sentData);
        assertEquals(clientId, sentData.getClientId());
        assertNotNull(sentData.getFirstName());
        assertNotNull(sentData.getLastName());

        System.out.println("Mocked and sent client data for ID: " + clientId + " to the queue: testQueue");
    }
}