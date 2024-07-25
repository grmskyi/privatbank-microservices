package com.example.second_receiver_service.services.impls;

import com.example.second_receiver_service.dtos.ClientDataDTO;
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
class SecondReceiverServiceImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private SecondReceiverServiceImpl secondReceiverService;


    @BeforeEach
    void setUp() {
        secondReceiverService = new SecondReceiverServiceImpl(rabbitTemplate);
        secondReceiverService.setQueueForProducer("testQueue");
    }

    @Test
    void mockAndSendUpdatedClientDataToThirdReceiverService_sendsEnhancedData() {
        ClientDataDTO originalData = ClientDataDTO.builder()
                .clientId("12345")
                .firstName("John")
                .lastName("Doe")
                .build();

        ArgumentCaptor<ClientDataDTO> dataCaptor = ArgumentCaptor.forClass(ClientDataDTO.class);

        secondReceiverService.mockAndSendUpdatedClientDataToThirdReceiverService(originalData);

        verify(rabbitTemplate).convertAndSend(eq("testQueue"), dataCaptor.capture());
        ClientDataDTO enhancedData = dataCaptor.getValue();

        assertNotNull(enhancedData.getEmail());
        assertEquals("john.doe@example.com", enhancedData.getEmail());
        assertNotNull(enhancedData.getAddress());
        assertEquals("123 Main Street", enhancedData.getAddress());
        assertNotNull(enhancedData.getPhoneNumber());
        assertEquals("555-555-5555", enhancedData.getPhoneNumber());

        System.out.println("Verified that the updated client data sent to the queue includes enhanced details.");
    }
}