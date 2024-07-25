package com.example.third_receiver_service.services.impls;

import com.example.third_receiver_service.dtos.ClientDataDTO;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class ThirdReceiverServiceImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ThirdReceiverServiceImpl thirdReceiverService;


    @BeforeEach
    void setUp() {
        thirdReceiverService = new ThirdReceiverServiceImpl(rabbitTemplate);
        thirdReceiverService.setQueueForProducer("testQueue");
    }

    @Test
    void mockAndSendUpdatedClientDataTofFurthReceiverService_sendsEnhancedData() {

        ClientDataDTO originalData = ClientDataDTO.builder()
                .clientId("12345")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .address("123 Main Street")
                .phoneNumber("555-1234")
                .build();

        ArgumentCaptor<ClientDataDTO> dataCaptor = ArgumentCaptor.forClass(ClientDataDTO.class);

        thirdReceiverService.mockAndSendUpdatedClientDataTofFurthReceiverService(originalData);

        verify(rabbitTemplate).convertAndSend(eq("testQueue"), dataCaptor.capture());
        ClientDataDTO enhancedData = dataCaptor.getValue();

        assertNotNull(enhancedData.getSavedContacts());
        assertEquals(2, enhancedData.getSavedContacts().size());
        assertEquals("5555 3333 6666 8888", enhancedData.getCardNumbers().get(0));
        assertEquals("Valeria", enhancedData.getSavedContacts().get(0).getContactName());

        System.out.println("Verified that the updated client data sent to the queue includes additional contacts and card numbers.");
    }
}