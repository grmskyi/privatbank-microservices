package com.example.sender_service.controllers;

import com.example.sender_service.services.ClientDataService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ClientDataSenderController.class)
class ClientDataSenderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientDataService clientDataService;

    @Test
    @SneakyThrows
    void whenPostRequestToSend_thenVerifyResponse() {
        String clientId = "12345";

        System.out.println("Sending POST request to /api/v1/send with clientId: " + clientId);
        mockMvc.perform(post("/api/v1/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientId))
                .andExpect(status().isCreated())
                .andExpect(content().string(clientId))
                .andDo(result -> System.out.println("Response status: " + result.getResponse().getStatus() +
                        " with content: " + result.getResponse().getContentAsString()));

        Mockito.verify(clientDataService).sendClientData(clientId);
        System.out.println("Verified that sendClientData was called with clientId: " + clientId);
    }
}