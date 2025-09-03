package dev.artisra.studysessionservice.controllers;

import dev.artisra.studysessionservice.security.SecurityConfiguration;
import dev.artisra.studysessionservice.services.interfaces.StudySessionService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@WebMvcTest(StudySessionController.class)
@Import(SecurityConfiguration.class)
class StudySessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudySessionService studySessionService;

    @Test
    void testCreateStudySession() throws Exception {
        when(studySessionService.createStudySession(any())).thenReturn(12345L);

        String newStudySessionJson = """
                {
                 	"userId": 4,
                 	"subjectId": 10002,
                 	"topicId": 111136,
                 	"issuedAt": "2025-09-02T18:30:00Z"
                }
                """;

        var request = post("/api/sessions/")
                .contentType("application/json")
                .content(newStudySessionJson);

        mockMvc.perform(request)
                .andExpectAll(
                        status().isCreated(),
                        result -> {
                            // Assert headers
                            String location = result.getResponse().getHeader("Location");
                            assert location != null;
                            assert location.equals("api/sessions/12345");
                            // Assert body
                            String responseBody = result.getResponse().getContentAsString();
                            assert responseBody.equals("12345");

                        }
                );

        verify(studySessionService, times(1)).createStudySession(any());
    }

    @Test
    void createStudySession_InvalidInput() throws Exception {
        String invalidStudySessionJson = """
                {
                 	"userId": -1,
                 	"subjectId": 10002,
                 	"topicId": 111136,
                 	"issuedAt": "2025-09-02T18:30:00Z"
                }
                """;

        var request = post("/api/sessions/")
                .contentType("application/json")
                .content(invalidStudySessionJson);

        mockMvc.perform(request)
                .andExpectAll(
                        status().isBadRequest(),
                        // Optionally, verify error message content
                         result -> {
                             String responseBody = result.getResponse().getContentAsString();
                             assert responseBody.contains("User ID must be a positive integer");
                         }

                );

        verify(studySessionService, times(0)).createStudySession(any());
    }

    @Test
    void testSendCommand() throws Exception {
        // When the service is called, do nothing
        doNothing().when(studySessionService).sendCommand(anyLong(), any());

        String commandJson = """
                {
                 	"commandId": "asbc-1234-defg-5678",
                 	"command": "START",
                 	"issuedBy": "user-42",
                 	"issuedAt": "2025-09-02T18:30:00Z"
                }
                """;

        var request = post("/api/sessions/{sessionId}/commands", 12345)
                .contentType("application/json")
                .content(commandJson);

        mockMvc.perform(request)
                .andExpect(status().isAccepted());

        verify(studySessionService, times(1)).sendCommand(eq(12345L), any());
    }

    @Test
    void testSendCommand_InvalidInput() throws Exception {
        String invalidCommandJson = """
                {
                 	"commandId": "asbc-1234-defg-5678",
                 	"command": "INVALID_COMMAND",
                 	"issuedBy": "user-42",
                 	"issuedAt": "2025-09-02T18:30:00Z"
                }
                """;

        var request = post("/api/sessions/{sessionId}/commands", 12345)
                .contentType("application/json")
                .content(invalidCommandJson);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());

        verify(studySessionService, times(0)).sendCommand(anyLong(), any());
    }
}
