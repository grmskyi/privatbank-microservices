package com.example.data_storage_service.configs;

import com.example.data_storage_service.dtos.ClientDataDTO;
import com.example.data_storage_service.dtos.SavedContactsDTO;
import com.example.data_storage_service.mappers.ClientDataMapper;
import com.example.data_storage_service.models.ClientData;
import com.example.data_storage_service.models.SavedContact;
import com.example.data_storage_service.services.DataStorageService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class RabbitMQDataStorageConfigurationTest {

    @Mock
    private ClientDataMapper clientDataMapper;
    @Mock
    private DataStorageService dataStorageService;
    @InjectMocks
    private RabbitMQDataStorageConfiguration configuration;

    @Test
    void listen_shouldConvertAndSaveClientData() {

        ClientDataDTO clientDataDTO = ClientDataDTO.builder()
                .clientId("123")
                .firstName("Joe")
                .lastName("Biden")
                .email("email@example.com")
                .address("some adress")
                .phoneNumber("some phone number")
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

        ClientData clientData = ClientData.builder()
                .clientId("123")
                .firstName("Joe")
                .lastName("Biden")
                .email("email@example.com")
                .address("some adress")
                .phoneNumber("some phone number")
                .savedContacts(List.of(
                        SavedContact
                                .builder()
                                .contactNumber("555 888 333 444")
                                .contactName("Valeria")
                                .build(),
                        SavedContact
                                .builder()
                                .contactNumber("666 777 444 333")
                                .contactName("Jack")
                                .build()
                ))
                .cardNumbers(List.of("5555 3333 6666 8888", "2222 1111 4444 5555"))
                .build();

        given(clientDataMapper.toClientData(clientDataDTO)).willReturn(clientData);

        configuration.listen(clientDataDTO);

        verify(clientDataMapper).toClientData(clientDataDTO);
        verify(dataStorageService).saveClientData(clientData);
        System.out.println("Verified: DTO is converted and data is saved.");
    }
}