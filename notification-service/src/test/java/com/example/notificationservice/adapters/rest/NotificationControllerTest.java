package com.example.notificationservice.adapters.rest;

import com.example.notificationservice.application.NotificationService;
import com.example.notificationservice.domain.NotificationRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationControllerTest {

    private MockMvc mockMvc;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = Mockito.mock(NotificationService.class);
        NotificationController controller = new NotificationController(notificationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shouldSendNotificationWhenValidRequest() throws Exception {
        String json = """
            {
              "recipient": "+48123456789",
              "message": "Test message",
              "channel": "SMS"
            }
        """;

        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        NotificationRequestDto expectedDto = new NotificationRequestDto("+48123456789", "Test message", "SMS");
        verify(notificationService, times(1)).send(Mockito.refEq(expectedDto));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidPhoneNumber() throws Exception {
        String json = """
            {
              "recipient": "123",
              "message": "Invalid phone",
              "channel": "SMS"
            }
        """;

        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(notificationService, times(0)).send(Mockito.any());
    }

    @Test
    void shouldReturnBadRequestWhenMissingFields() throws Exception {
        String json = """
            {
              "recipient": "+48123456789"
            }
        """;

        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(notificationService, times(0)).send(Mockito.any());
    }
}

