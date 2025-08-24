package dev.artisra.studysessionservice.controllers;

import dev.artisra.studysessionservice.models.dto.ActiveStudySession;
import dev.artisra.studysessionservice.models.enums.StudySessionState;
import dev.artisra.studysessionservice.services.interfaces.StudySessionService;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@WebMvcTest(StudyServiceController.class)
class StudyServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudySessionService studySessionService;

    @Test
    void testCreateStudySession() throws Exception {
        when(studySessionService.createStudySession(any())).thenReturn(1L);

        var request = post("/api/sessions/new")
                .contentType("application/json")
                .content("{\"subject\":\"Math\",\"topic\":\"Algebra\",\"userId\":1}");

        mockMvc.perform(request)
                .andExpect(status().isCreated());
    }

    @Disabled
    @Test
    void testCreateStudySession_EmptyData() throws Exception {
        var requestEmptyData = post("/api/sessions/new")
                .contentType("application/json")
                .content("{\"subject\":\"\",\"topic\":\"\",\"userId\":\"\"}"); // Assumes validation on the DTO

        mockMvc.perform(requestEmptyData)
                .andExpect(status().isBadRequest());
    }

    @Disabled
    @Test
    void testCreateStudySession_MissingFields() throws Exception {
        var requestMissingFields = post("/api/sessions/new")
                .contentType("application/json")
                .content("{\"subject\":\"\",\"userId\":\"\"}"); // Missing 'topic'

        mockMvc.perform(requestMissingFields)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSendCommand_Success() throws Exception {
        when(studySessionService.exists(1L)).thenReturn(true);

        doNothing().when(studySessionService).sendCommand(eq(1L), any());

        var request = post("/api/sessions/1/command")
                .contentType("application/json")
                .content("{\"command\":\"START\"}");
        mockMvc.perform(request)
                .andExpect(status().isAccepted());
    }

    @Test
    void testSendCommand_SessionNotFound() throws Exception {
        when(studySessionService.exists(1L)).thenReturn(false);

        var request = post("/api/sessions/1/command")
                .contentType("application/json")
                .content("{\"command\":\"START\"}");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void testSendCommand_InvalidCommand() throws Exception {
        when(studySessionService.exists(1L)).thenReturn(true);
        // Mock the sendCommand to throw an exception for an invalid command
        doThrow(new IllegalArgumentException("Invalid command")).when(studySessionService).sendCommand(eq(1L), any());

        var request = post("/api/sessions/1/command")
                .contentType("application/json")
                .content("{\"command\":\"INVALID_COMMAND\"}");

        mockMvc.perform(request)
                .andExpect(status().isBadRequest()); // Expect a 400 Bad Request
    }

    @Test
    void testCreateStudySession_MalformedJson() throws Exception {
        var malformedRequest = post("/api/sessions/new")
                .contentType("application/json")
                .content("{\"subject\":\"Math\"\"topic\":\"Algebra\"}"); // Missing comma

        mockMvc.perform(malformedRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSendCommand_MalformedJson() throws Exception {
        when(studySessionService.exists(1L)).thenReturn(true);
        var malformedRequest = post("/api/sessions/1/command")
                .contentType("application/json")
                .content("{\"command\" \"START\"}"); // Missing colon

        mockMvc.perform(malformedRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetActiveStudySessionById_NotFound() throws Exception {
        when(studySessionService.exists(1L)).thenReturn(false); // Mocking the existence check

        var request = get("/api/sessions/1")
                .contentType("application/json");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetActiveStudySessionById_NullActiveStudySession() throws Exception {
        when(studySessionService.exists(1L)).thenReturn(true);
        when(studySessionService.getActiveStudySession(1L)).thenReturn(null); // Mock the service to return null

        var request = get("/api/sessions/1")
                .contentType("application/json");

        mockMvc.perform(request)
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found
    }

    @Test
    void testGetActiveStudySessions_EmptyList() throws Exception {
        when(studySessionService.getAllStudySessions()).thenReturn(List.of()); // Mock an empty list

        var request = get("/api/sessions/active")
                .contentType("application/json");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    assert jsonResponse.equals("[]"); // Assert that the response is an empty JSON array
                });
    }

    @Test
    void testGetActiveStudySessionById_Success() throws Exception {
        // Mocking the existence check
        when(studySessionService.exists(1L)).thenReturn(true);

        ActiveStudySession mockSession = new ActiveStudySession(1L, "Piano", "Scales", "artisra",
                StudySessionState.IN_PROGRESS);

        // This is the correct mock call. The method should return the mock session.
        when(studySessionService.getActiveStudySession(1L)).thenReturn(mockSession);

        var request = get("/api/sessions/1")
                .contentType("application/json");

        mockMvc.perform(request)
                .andExpectAll(result -> {
                    // Verify status
                    status().isOk().match(result);
                    // Verify response content
                    String jsonResponse = result.getResponse().getContentAsString();
                    assert jsonResponse.contains("\"id\":1");
                    assert jsonResponse.contains("\"subject\":\"Piano\"");
                    assert jsonResponse.contains("\"topic\":\"Scales\"");
                    assert jsonResponse.contains("\"userId\":\"artisra\"");
                    assert jsonResponse.contains("\"state\":\"IN_PROGRESS\"");
                });
    }

    @Test
    void testGetActiveStudySessions() throws Exception {
        List<ActiveStudySession> mockSessions = List.of(
                new ActiveStudySession(1L, "Piano", "Scales", "artisra", StudySessionState.IN_PROGRESS),
                new ActiveStudySession(2L, "Guitar", "Chords", "imendoza", StudySessionState.NOT_STARTED));

        when(studySessionService.getAllStudySessions()).thenReturn(mockSessions);

        var request = get("/api/sessions/active")
                .contentType("application/json");

        mockMvc.perform(request)
                .andExpectAll(result -> {
                    status().isOk().match(result);
                    String jsonResponse = result.getResponse().getContentAsString();
                    assert jsonResponse.contains("\"id\":1");
                    assert jsonResponse.contains("\"subject\":\"Piano\"");
                    assert jsonResponse.contains("\"topic\":\"Scales\"");
                    assert jsonResponse.contains("\"userId\":\"artisra\"");
                    assert jsonResponse.contains("\"state\":\"IN_PROGRESS\"");
                    assert jsonResponse.contains("\"id\":2");
                    assert jsonResponse.contains("\"subject\":\"Guitar\"");
                    assert jsonResponse.contains("\"topic\":\"Chords\"");
                    assert jsonResponse.contains("\"userId\":\"imendoza\"");
                    assert jsonResponse.contains("\"state\":\"NOT_STARTED\"");
                });
    }
}
