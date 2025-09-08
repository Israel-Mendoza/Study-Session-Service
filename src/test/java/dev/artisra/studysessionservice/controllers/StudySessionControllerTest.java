package dev.artisra.studysessionservice.controllers;

import dev.artisra.studysessionservice.models.dto.CommandRequest;
import dev.artisra.studysessionservice.security.SecurityConfiguration;
import dev.artisra.studysessionservice.services.interfaces.CommandIdService;
import dev.artisra.studysessionservice.services.interfaces.StudySessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudySessionController.class)
@Import(SecurityConfiguration.class)
public class StudySessionControllerTest {
    @MockitoBean
    private CommandIdService commandIdService;

    @MockitoBean
    private StudySessionService studySessionService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateStudySession_Returns202() throws Exception {
        // Do nothing when studySessionService.createStudySession is called with any argument
        doNothing().when(studySessionService).createStudySession(any());

        String newStudySessionJSON = """
                {
                    "commandId": "%s",
                    "userId": "%s",
                    "subjectId": "%s",
                    "topicId": "%s"
                }
                """.formatted("0db1eec6-ec42-45e4-aee9-cf7001dd87a3", 4, 10002, 111136);

        var request = post("/api/sessions/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newStudySessionJSON);

        mockMvc.perform(request)
                .andExpect(status().isAccepted());
    }

    @Test
    public void testCreateStudySession_Returns208() throws Exception {
        String processedCommandId = "c6148bb0-168a-4779-80f0-8a01d5278196";

        when(commandIdService.isProcessed(processedCommandId)).thenReturn(true);

        String newStudySessionJSON = """
                {
                    "commandId": "%s",
                    "userId": "%s",
                    "subjectId": "%s",
                    "topicId": "%s"
                }
        """.formatted("c6148bb0-168a-4779-80f0-8a01d5278196", 5, 10008, 111139);

        var request = post("/api/sessions/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newStudySessionJSON);

        mockMvc.perform(request)
                .andExpect(status().isAlreadyReported())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.statusCode").value(208))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.cause.message", containsString(processedCommandId)));
    }

    @Test
    public void testCreateStudySession_withInvalidCommandId_Returns400() throws Exception {
        String newStudySessionJSON = """
                {
                    "commandId": "invalid-uuid",
                    "userId": 4,
                    "subjectId": 10002,
                    "topicId": 111136
                }
                """;

        mockMvc.perform(post("/api/sessions/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newStudySessionJSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.cause.commandId").value("Command ID must be a valid UUID"));
    }

    @Test
    public void testCreateStudySession_withNegativeUserId_Returns400() throws Exception {
        String newStudySessionJSON = """
                {
                    "commandId": "0db1eec6-ec42-45e4-aee9-cf7001dd87a3",
                    "userId": -1,
                    "subjectId": 10002,
                    "topicId": 111136
                }
                """;

        mockMvc.perform(post("/api/sessions/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newStudySessionJSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.cause.userId").value("User ID must be a positive integer"));
    }

    @Test
    public void testCreateStudySession_withNegativeSubjectId_Returns400() throws Exception {
        String newStudySessionJSON = """
                {
                    "commandId": "0db1eec6-ec42-45e4-aee9-cf7001dd87a3",
                    "userId": 4,
                    "subjectId": -1,
                    "topicId": 111136
                }
                """;

        mockMvc.perform(post("/api/sessions/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newStudySessionJSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.cause.subjectId").value("Subject ID must be a positive integer"));
    }

    @Test
    public void testCreateStudySession_withNegativeTopicId_Returns400() throws Exception {
        String newStudySessionJSON = """
                {
                    "commandId": "0db1eec6-ec42-45e4-aee9-cf7001dd87a3",
                    "userId": 4,
                    "subjectId": 10002,
                    "topicId": -1
                }
                """;

        mockMvc.perform(post("/api/sessions/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newStudySessionJSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.cause.topicId").value("Topic ID must be a positive integer"));
    }

    @Test
    public void testCreateStudySession_withMultipleValidationErrors_Returns400() throws Exception {
        String newStudySessionJSON = """
                {
                    "commandId": "0db1eec6-42-45e4-aee9-cf7001dd87a3",
                    "userId": -4,
                    "subjectId": -10002,
                    "topicId": -1
                }
                """;

        mockMvc.perform(post("/api/sessions/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newStudySessionJSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.cause.topicId").value("Topic ID must be a positive integer"))
                .andExpect(jsonPath("$.cause.commandId").value("Command ID must be a valid UUID"))
                .andExpect(jsonPath("$.cause.userId").value("User ID must be a positive integer"))
                .andExpect(jsonPath("$.cause.subjectId").value("Subject ID must be a positive integer"));
    }

    @Test
    public void testSendCommand_Returns202() throws Exception {
        long sessionId = 12345L;
        String commandJson = """
                {
                    "id": "19e57c74-be9c-4212-8865-e105a075dc05",
                    "command": "START",
                    "issuedBy": "test-user"
                }
                """;
        // Correctly stubbing the methods
        when(commandIdService.isProcessed(any())).thenReturn(false);
        doNothing().when(studySessionService).sendCommand(any(Long.class), any(CommandRequest.class));

        var request = post("/api/sessions/" + sessionId + "/commands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(commandJson);

        mockMvc.perform(request)
                .andExpect(status().isAccepted());
    }

    @Test
    public void testSendCommand_Returns208() throws Exception {
        long sessionId = 12345L;
        String processedCommandId = "19e57c74-be9c-4212-8865-e105a075dc05";
        when(commandIdService.isProcessed(processedCommandId)).thenReturn(true);

        String commandJson = """
                {
                    "id": "%s",
                    "command": "START",
                    "issuedBy": "test-user"
                }
                """.formatted(processedCommandId);

        var request = post("/api/sessions/{sessionId}/commands", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(commandJson);

        mockMvc.perform(request)
                .andExpect(status().isAlreadyReported())
                .andExpect(jsonPath("$.statusCode").value(208))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.cause.message", containsString(processedCommandId)));
    }

    @Test
    public void testSendCommand_withInvalidCommandId_Returns400() throws Exception {
        long sessionId = 12345L;
        String commandJson = """
                {
                    "id": "invalid-uuid",
                    "command": "START",
                    "issuedBy": "test-user"
                }
                """;

        var request = post("/api/sessions/{sessionId}/commands", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(commandJson);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.cause.id").value("Command ID must be a valid UUID"));
    }

    @Test
    public void testSendCommand_withNullCommand_Returns400() throws Exception {
        long sessionId = 12345L;
        String commandJson = """
                {
                    "id": "19e57c74-be9c-4212-8865-e105a075dc05",
                    "command": null,
                    "issuedBy": "test-user"
                }
                """;

        var request = post("/api/sessions/{sessionId}/commands", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(commandJson);

        mockMvc.perform(request)
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.cause.command").value("Command cannot be null"));
    }

    @Test
    public void testSendCommand_withNullIssuedBy_Returns400() throws Exception {
        long sessionId = 12345L;
        String commandJson = """
                {
                    "id": "19e57c74-be9c-4212-8865-e105a075dc05",
                    "command": "START",
                    "issuedBy": null
                }
                """;

        var request = post("/api/sessions/{sessionId}/commands", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(commandJson);

        mockMvc.perform(request)
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.cause.issuedBy").value("Issued By cannot be null"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "abcdefghijklmnopqrsabcdefghijklmnopqrsabcdefghijklmnopqrs"})
    public void testSendCommand_withInvalidIssuedBySize_Returns400(String issuedBy) throws Exception {
        long sessionId = 12345L;
        String commandJson = String.format("""
                {
                    "id": "19e57c74-be9c-4212-8865-e105a075dc05",
                    "command": "START",
                    "issuedBy": "%s"
                }
                """, issuedBy);

        var request = post("/api/sessions/{sessionId}/commands", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(commandJson);

        mockMvc.perform(request)
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.cause.issuedBy").value("Issued By must be between 6 and 50 characters"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"BANANA", "RESTART"})
    public void testSendCommand_withIncorrectCommand_Returns400(String command) throws Exception {
        long sessionId = 12345L;
        String commandJson = String.format("""
                {
                    "id": "19e57c74-be9c-4212-8865-e105a075dc05",
                    "command": "%s",
                    "issuedBy": "test-user"
                }
                """, command);

        var request = post("/api/sessions/{sessionId}/commands", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(commandJson);

        mockMvc.perform(request)
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.cause.command").value("Invalid command value. Must be one of: START, PAUSE, RESUME, COMPLETE, CANCEL, or DELETE"));
    }
}
