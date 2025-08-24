package dev.artisra.studysessionservice.services.impl;

import dev.artisra.studysessionservice.models.dto.StudySessionRequest;
import dev.artisra.studysessionservice.models.dto.ActiveStudySession;
import dev.artisra.studysessionservice.models.dto.CommandRequest;
import dev.artisra.studysessionservice.models.enums.StudySessionCommand;
import dev.artisra.studysessionservice.models.enums.StudySessionState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocalStudySessionServiceTest {

    @Test
    void testCreateAndGetOneStudySession() {
        LocalStudySessionService service = new LocalStudySessionService();
        StudySessionRequest req = new StudySessionRequest("artisra", "Math", "Algebra");
        long id = service.createStudySession(req);

        assertTrue(service.exists(id));
        ActiveStudySession session = service.getActiveStudySession(id);
        assertEquals("Math", session.getSubject());
        assertEquals(StudySessionState.NOT_STARTED, session.getState());
        assertEquals("artisra", session.getUserId());
    }

    @Test
    void testCreateAndGetMultipleStudySession() {
        LocalStudySessionService service = new LocalStudySessionService();
        StudySessionRequest req1 = new StudySessionRequest("artisra", "Math", "Algebra");
        StudySessionRequest req2 = new StudySessionRequest("bocanvi", "German", "Anki");
        StudySessionRequest req3 = new StudySessionRequest("imendoza", "Piano", "Scales");
        long id1 = service.createStudySession(req1);
        long id2 = service.createStudySession(req2);
        long id3 = service.createStudySession(req3);

        // Assert all three exist
        assertTrue(service.exists(id1));
        assertTrue(service.exists(id2));
        assertTrue(service.exists(id3));

        // All three sessions can be retrieved and have correct data
        ActiveStudySession session1 = service.getActiveStudySession(id1);
        ActiveStudySession session2 = service.getActiveStudySession(id2);
        ActiveStudySession session3 = service.getActiveStudySession(id3);

        // Check session 1
        assertEquals("Math", session1.getSubject());
        assertEquals(StudySessionState.NOT_STARTED, session1.getState());
        assertEquals("artisra", session1.getUserId());

        // Check session 2
        assertEquals("German", session2.getSubject());
        assertEquals(StudySessionState.NOT_STARTED, session2.getState());
        assertEquals("bocanvi", session2.getUserId());

        // Check session 3
        assertEquals("Piano", session3.getSubject());
        assertEquals(StudySessionState.NOT_STARTED, session3.getState());
        assertEquals("imendoza", session3.getUserId());
    }

    @Test
    void testGetNonExistentStudySession() {
        LocalStudySessionService service = new LocalStudySessionService();
        assertFalse(service.exists(999L));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.getActiveStudySession(999L);
        });
        String expectedMessage = "No active study session found with ID 999";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testSendStartCommandHappyPath() {
        LocalStudySessionService service = new LocalStudySessionService();
        StudySessionRequest req = new StudySessionRequest("artisra", "Math", "Algebra");
        long id = service.createStudySession(req);

        // Start the session
        service.sendCommand(id, new CommandRequest(StudySessionCommand.START));
        ActiveStudySession session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.IN_PROGRESS, session.getState());

        // Pause the session
        service.sendCommand(id, new CommandRequest(StudySessionCommand.PAUSE));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.PAUSED, session.getState());

        // Resume the session
        service.sendCommand(id, new CommandRequest(StudySessionCommand.RESUME));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.IN_PROGRESS, session.getState());

        // Complete the session
        service.sendCommand(id, new CommandRequest(StudySessionCommand.COMPLETE));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.COMPLETED, session.getState());

        // Attempt to cancel a completed session (should have no effect)
        service.sendCommand(id, new CommandRequest(StudySessionCommand.CANCEL));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.COMPLETED, session.getState());

        // Delete the session
        service.sendCommand(id, new CommandRequest(StudySessionCommand.DELETE));
        assertFalse(service.exists(id));
    }

    @Test
    void testSendInvalidCommands() {
        LocalStudySessionService service = new LocalStudySessionService();
        StudySessionRequest req = new StudySessionRequest("artisra", "Piano", "Scales");
        long id = service.createStudySession(req);

        // Attempt to pause before starting
        service.sendCommand(id, new CommandRequest(StudySessionCommand.PAUSE));
        ActiveStudySession session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.NOT_STARTED, session.getState());

        // Attempt to resume before starting
        service.sendCommand(id, new CommandRequest(StudySessionCommand.RESUME));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.NOT_STARTED, session.getState());

        // Attempt to complete before starting
        service.sendCommand(id, new CommandRequest(StudySessionCommand.COMPLETE));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.NOT_STARTED, session.getState());

        // Attempt to cancel before starting should cancel the session
        service.sendCommand(id, new CommandRequest(StudySessionCommand.CANCEL));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.CANCELLED, session.getState());

        // Attempt to start after cancelling
        service.sendCommand(id, new CommandRequest(StudySessionCommand.START));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.CANCELLED, session.getState());
    }

    @Test
    void testSendCancelCommandAtAnyStage() {
        LocalStudySessionService service = new LocalStudySessionService();
        StudySessionRequest req = new StudySessionRequest("artisra", "Piano", "Scales");
        long id = service.createStudySession(req);
        ActiveStudySession session = service.getActiveStudySession(id);

        // Cancel before starting
        service.sendCommand(id, new CommandRequest(StudySessionCommand.CANCEL));
        assertEquals(StudySessionState.CANCELLED, session.getState());

        // Starting the session and then cancelling
        service.sendCommand(id, new CommandRequest(StudySessionCommand.START));
        service.sendCommand(id, new CommandRequest(StudySessionCommand.CANCEL));
        assertEquals(StudySessionState.CANCELLED, session.getState());

        // Creating new session to cancel while paused
        long id2 = service.createStudySession(req);
        service.sendCommand(id2, new CommandRequest(StudySessionCommand.START));
        service.sendCommand(id2, new CommandRequest(StudySessionCommand.PAUSE));
        service.sendCommand(id2, new CommandRequest(StudySessionCommand.CANCEL));
        session = service.getActiveStudySession(id2);
        assertEquals(StudySessionState.CANCELLED, session.getState());

        // Creating new session to cancel while resumed
        long id3 = service.createStudySession(req);
        service.sendCommand(id3, new CommandRequest(StudySessionCommand.START));
        service.sendCommand(id3, new CommandRequest(StudySessionCommand.PAUSE));
        service.sendCommand(id3, new CommandRequest(StudySessionCommand.RESUME));
        service.sendCommand(id3, new CommandRequest(StudySessionCommand.CANCEL));
        session = service.getActiveStudySession(id3);
        assertEquals(StudySessionState.CANCELLED, session.getState());

        // Creating new session to cancel while completed (should have no effect)
        long id4 = service.createStudySession(req);
        service.sendCommand(id4, new CommandRequest(StudySessionCommand.START));
        service.sendCommand(id4, new CommandRequest(StudySessionCommand.COMPLETE));
        service.sendCommand(id4, new CommandRequest(StudySessionCommand.CANCEL));
        session = service.getActiveStudySession(id4);
        assertEquals(StudySessionState.COMPLETED, session.getState());

        // Deleting a cancelled session
        service.sendCommand(id, new CommandRequest(StudySessionCommand.DELETE));
        assertFalse(service.exists(id));
    }

    @Test
    void testSendCommandToNonExistentSession() {
        LocalStudySessionService service = new LocalStudySessionService();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.sendCommand(999L, new CommandRequest(StudySessionCommand.START));
        });
        String expectedMessage = "No active study session found with ID 999";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testSendCommandsOutOfOrder() {
        LocalStudySessionService service = new LocalStudySessionService();
        StudySessionRequest req = new StudySessionRequest("artisra", "Math", "Algebra");
        long id = service.createStudySession(req);

        // Attempt to resume before starting
        service.sendCommand(id, new CommandRequest(StudySessionCommand.RESUME));
        ActiveStudySession session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.NOT_STARTED, session.getState());

        // Start the session
        service.sendCommand(id, new CommandRequest(StudySessionCommand.START));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.IN_PROGRESS, session.getState());

        // Attempt to start again
        service.sendCommand(id, new CommandRequest(StudySessionCommand.START));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.IN_PROGRESS, session.getState());

        // Pause the session
        service.sendCommand(id, new CommandRequest(StudySessionCommand.PAUSE));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.PAUSED, session.getState());

        // Attempt to start while paused
        service.sendCommand(id, new CommandRequest(StudySessionCommand.START));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.PAUSED, session.getState());

        // Attempt to pause again
        service.sendCommand(id, new CommandRequest(StudySessionCommand.PAUSE));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.PAUSED, session.getState());

        // Resume the session
        service.sendCommand(id, new CommandRequest(StudySessionCommand.RESUME));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.IN_PROGRESS, session.getState());

        // Attempt to start while in progress
        service.sendCommand(id, new CommandRequest(StudySessionCommand.START));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.IN_PROGRESS, session.getState());

        // Attempt to resume while in progress
        service.sendCommand(id, new CommandRequest(StudySessionCommand.RESUME));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.IN_PROGRESS, session.getState());

        // Complete the session
        service.sendCommand(id, new CommandRequest(StudySessionCommand.COMPLETE));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.COMPLETED, session.getState());

        // Attempt to complete again
        service.sendCommand(id, new CommandRequest(StudySessionCommand.COMPLETE));
        session = service.getActiveStudySession(id);
        assertEquals(StudySessionState.COMPLETED, session.getState());
    }
}