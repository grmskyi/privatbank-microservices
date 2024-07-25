package com.example.third_receiver_service.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDataDTO {
    private String clientId;
    private String firstName;
    private String lastName;

    private String email;
    private String phoneNumber;
    private String address;

    private List<String> cardNumbers;
    private List<SavedContactsDTO> savedContacts;
}