//package com.simpleAnalytics.EventConsumer;
//
//import java.sql.Timestamp;
//import java.time.Instant;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.anyList;
//import static org.mockito.ArgumentMatchers.anyString;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import static org.mockito.Mockito.atLeastOnce;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.dao.DataAccessException;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import com.simpleAnalytics.EventConsumer.MQ.DLQEventProducer;
//import com.simpleAnalytics.EventConsumer.entity.Context;
//import com.simpleAnalytics.EventConsumer.entity.Event;
//import com.simpleAnalytics.EventConsumer.entity.EventBuffer;
//import com.simpleAnalytics.EventConsumer.entity.SchemaVersion;
//import com.simpleAnalytics.EventConsumer.entity.UserEvent;
//import com.simpleAnalytics.EventConsumer.repository.EventRepositoryImpl;
//
//@ExtendWith(MockitoExtension.class)
//public class EventRepositoryTest {
//
//    @Mock
//    private JdbcTemplate jdbcTemplate;
//
//    @Mock
//    private DLQEventProducer dlqEventProducer;
//
//    @Mock
//    private EventBuffer eventBuffer;
//
//    @InjectMocks
//    private EventRepositoryImpl eventRepository;
//
//    private Event testEvent;
//    private Context testContext;
//    private UserEvent testUserEvent;
//
//    @BeforeEach
//    void setUp() {
//        testContext = createTestContext();
//        testUserEvent = createTestUserEvent();
//        testEvent = createTestEvent();
//    }
//
//    @Test
//    void testSaveEvent_ShouldAddEventToBuffer() {
//        // Arrange
//        when(eventBuffer.shouldFlush()).thenReturn(false);
//
//        // Act
//        eventRepository.save(testEvent);
//
//        // Assert
//        verify(eventBuffer).add(testEvent);
//        verify(eventBuffer).shouldFlush();
//    }
//
//    @Test
//    void testSaveEvent_ShouldFlushWhenBufferShouldFlush() {
//        // Arrange
//        when(eventBuffer.shouldFlush()).thenReturn(true);
//        when(eventBuffer.isEmpty()).thenReturn(false);
//        when(eventBuffer.drainBatch(500))
//                .thenReturn(List.of(testEvent))
//                .thenReturn(List.of());
//
//        // Act
//        eventRepository.save(testEvent);
//
//        // Assert
//        verify(eventBuffer).add(testEvent);
//        verify(eventBuffer).shouldFlush();
//        verify(jdbcTemplate).batchUpdate(anyString(), anyList(), anyInt(), any());
//    }
//
//    @Test
//    void testSaveAll_ShouldAddAllEventsToBuffer() {
//        // Arrange
//        Event secondEvent = Event.builder()
//                .Id(UUID.randomUUID())
//                .receivedAt(Timestamp.from(Instant.now()))
//                .context(testContext)
//                .schemaVersion(SchemaVersion.V1_0_0)
//                .userEvent(testUserEvent)
//                .build();
//        List<Event> events = Arrays.asList(testEvent, secondEvent);
//        when(eventBuffer.shouldFlush()).thenReturn(false);
//
//        // Act
//        eventRepository.saveAll(events);
//
//        // Assert
//        verify(eventBuffer).addAll(events);
//        verify(eventBuffer).shouldFlush();
//    }
//
//    @Test
//    void testSaveAll_ShouldFlushWhenBufferShouldFlush() {
//        // Arrange
//        Event secondEvent = Event.builder()
//                .Id(UUID.randomUUID())
//                .receivedAt(Timestamp.from(Instant.now()))
//                .context(testContext)
//                .schemaVersion(SchemaVersion.V1_0_0)
//                .userEvent(testUserEvent)
//                .build();
//        List<Event> events = Arrays.asList(testEvent, secondEvent);
//        when(eventBuffer.shouldFlush()).thenReturn(true);
//        when(eventBuffer.isEmpty()).thenReturn(false);
//        when(eventBuffer.drainBatch(500))
//                .thenReturn(events)
//                .thenReturn(List.of());
//
//        // Act
//        eventRepository.saveAll(events);
//
//        // Assert
//        verify(eventBuffer).addAll(events);
//        verify(eventBuffer).shouldFlush();
//        verify(jdbcTemplate).batchUpdate(anyString(), anyList(), anyInt(), any());
//    }
//
//    @Test
//    void testScheduledBatchSave_WhenBufferIsEmpty_ShouldNotFlush() {
//        // Arrange
//        when(eventBuffer.isEmpty()).thenReturn(true);
//
//        // Act
//        ReflectionTestUtils.invokeMethod(eventRepository, "scheduledBatchSave");
//
//        // Assert
//        verify(eventBuffer).isEmpty();
//        verify(eventBuffer, never()).drainBatch(anyInt());
//        verify(jdbcTemplate, never()).batchUpdate(anyString(), anyList(), anyInt(), any());
//    }
//
//    @Test
//    void testScheduledBatchSave_WhenBufferHasEvents_ShouldFlush() {
//        // Arrange
//        List<Event> events = List.of(testEvent);
//        when(eventBuffer.isEmpty()).thenReturn(false);
//        when(eventBuffer.drainBatch(500)).thenReturn(events, List.of());
//
//        // Act
//        ReflectionTestUtils.invokeMethod(eventRepository, "scheduledBatchSave");
//
//        // Assert
//        verify(eventBuffer).isEmpty();
//        verify(eventBuffer, atLeastOnce()).drainBatch(500);
//        verify(jdbcTemplate).batchUpdate(anyString(), anyList(), anyInt(), any());
//    }
//
//    @Test
//    void testSaveBatch_WithDatabaseException_ShouldSendToDLQ() {
//        // Arrange
//        List<Event> events = List.of(testEvent);
//        when(eventBuffer.isEmpty()).thenReturn(false);
//        when(eventBuffer.drainBatch(500)).thenReturn(events, List.of());
//        when(jdbcTemplate.batchUpdate(anyString(), anyList(), anyInt(), any()))
//                .thenThrow(new DataAccessException("Database error") {
//                });
//
//        // Act
//        ReflectionTestUtils.invokeMethod(eventRepository, "scheduledBatchSave");
//
//        // Assert
//        verify(dlqEventProducer).sendEvents(anyList());
//    }
//
//    @Test
//    void testEventWithNullContext_ShouldHandleGracefully() {
//        // Arrange
//        Event eventWithNullContext = Event.builder()
//                .Id(UUID.randomUUID())
//                .receivedAt(Timestamp.from(Instant.now()))
//                .context(null)
//                .schemaVersion(SchemaVersion.V1_0_0)
//                .userEvent(testUserEvent)
//                .build();
//
//        List<Event> events = List.of(eventWithNullContext);
//        when(eventBuffer.isEmpty()).thenReturn(false);
//        when(eventBuffer.drainBatch(500))
//                .thenReturn(events)
//                .thenReturn(List.of());
//
//        // Act
//        ReflectionTestUtils.invokeMethod(eventRepository, "scheduledBatchSave");
//
//        // Assert
//        verify(jdbcTemplate).batchUpdate(anyString(), anyList(), anyInt(), any());
//        // Verify that null context doesn't cause exceptions
//        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(eventRepository, "scheduledBatchSave"));
//    }
//
//    @Test
//    void testEventWithNullMetadata_ShouldHandleGracefully() {
//        // Arrange
//        UserEvent userEventWithNullMetadata = new UserEvent(
//                UUID.randomUUID(),
//                UUID.randomUUID(),
//                UUID.randomUUID(),
//                UUID.randomUUID(),
//                Timestamp.from(Instant.now()),
//                "page_view",
//                "web",
//                null
//        );
//
//        Event eventWithNullMetadata = Event.builder()
//                .Id(UUID.randomUUID())
//                .receivedAt(Timestamp.from(Instant.now()))
//                .context(testContext)
//                .schemaVersion(SchemaVersion.V1_0_0)
//                .userEvent(userEventWithNullMetadata)
//                .build();
//
//        List<Event> events = List.of(eventWithNullMetadata);
//        when(eventBuffer.isEmpty()).thenReturn(false);
//        when(eventBuffer.drainBatch(500))
//                .thenReturn(events)
//                .thenReturn(List.of());
//
//        // Act
//        ReflectionTestUtils.invokeMethod(eventRepository, "scheduledBatchSave");
//
//        // Assert
//        verify(jdbcTemplate).batchUpdate(anyString(), anyList(), anyInt(), any());
//        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(eventRepository, "scheduledBatchSave"));
//    }
//
//    // Helper methods for creating test data
//    private Context createTestContext() {
//        return Context.builder()
//                .ip("192.168.1.1")
//                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
//                .os("Windows")
//                .browser("Chrome")
//                .device("Desktop")
//                .locale("en-US")
//                .timezone("America/New_York")
//                .build();
//    }
//
//    private UserEvent createTestUserEvent() {
//        Map<String, Object> metadata = new HashMap<>();
//        metadata.put("page", "/home");
//        metadata.put("referrer", "https://google.com");
//
//        return new UserEvent(
//                UUID.randomUUID(),
//                UUID.randomUUID(),
//                UUID.randomUUID(),
//                UUID.randomUUID(),
//                Timestamp.from(Instant.now()),
//                "page_view",
//                "web",
//                metadata
//        );
//    }
//
//    private Event createTestEvent() {
//        return Event.builder()
//                .Id(UUID.randomUUID())
//                .receivedAt(Timestamp.from(Instant.now()))
//                .context(testContext)
//                .schemaVersion(SchemaVersion.V1_0_0)
//                .userEvent(testUserEvent)
//                .build();
//    }
//
//    @Test
//    void testBatchProcessing_WithMultipleBatches_ShouldProcessAll() {
//        // Arrange
//        List<Event> firstBatch = List.of(testEvent);
//        List<Event> secondBatch = List.of(createAnotherTestEvent());
//        when(eventBuffer.isEmpty()).thenReturn(false);
//        when(eventBuffer.drainBatch(500))
//                .thenReturn(firstBatch)
//                .thenReturn(secondBatch)
//                .thenReturn(Collections.emptyList());
//
//        // Act
//        ReflectionTestUtils.invokeMethod(eventRepository, "scheduledBatchSave");
//
//        // Assert
//        verify(eventBuffer, times(3)).drainBatch(500);
//        verify(jdbcTemplate, times(2)).batchUpdate(anyString(), anyList(), anyInt(), any());
//    }
//
//    @Test
//    void testErrorHandling_WithRetryableException_ShouldRetry() {
//        // Arrange
//        List<Event> events = List.of(testEvent);
//        when(eventBuffer.isEmpty()).thenReturn(false);
//        when(eventBuffer.drainBatch(500))
//                .thenReturn(events)
//                .thenReturn(Collections.emptyList());
//        when(jdbcTemplate.batchUpdate(anyString(), anyList(), anyInt(), any()))
//                .thenThrow(new DataAccessException("Retryable error") {
//                });
//
//        // Act
//        ReflectionTestUtils.invokeMethod(eventRepository, "scheduledBatchSave");
//
//        // Assert
//        verify(jdbcTemplate, times(1)).batchUpdate(anyString(), anyList(), anyInt(), any());
//        verify(dlqEventProducer).sendEvents(anyList());
//    }
//
//    @Test
//    void testFlushThreshold_WhenBufferShouldFlush_ShouldTriggerFlush() {
//        // Arrange
//        when(eventBuffer.shouldFlush()).thenReturn(true);
//        when(eventBuffer.isEmpty()).thenReturn(false);
//        when(eventBuffer.drainBatch(500))
//                .thenReturn(List.of(testEvent))
//                .thenReturn(Collections.emptyList());
//
//        // Act
//        eventRepository.save(testEvent);
//
//        // Assert
//        verify(eventBuffer).add(testEvent);
//        verify(eventBuffer).shouldFlush();
//        verify(jdbcTemplate).batchUpdate(anyString(), anyList(), anyInt(), any());
//    }
//
//    @Test
//    void testSaveAll_WithEmptyList_ShouldNotThrowException() {
//        // Arrange
//        List<Event> emptyEvents = Collections.emptyList();
//        when(eventBuffer.shouldFlush()).thenReturn(false);
//
//        // Act & Assert
//        assertDoesNotThrow(() -> eventRepository.saveAll(emptyEvents));
//        verify(eventBuffer).addAll(emptyEvents);
//        verify(eventBuffer).shouldFlush();
//    }
//
//    private Event createAnotherTestEvent() {
//        return Event.builder()
//                .Id(UUID.randomUUID())
//                .receivedAt(Timestamp.from(Instant.now()))
//                .context(testContext)
//                .schemaVersion(SchemaVersion.V1_0_0)
//                .userEvent(testUserEvent)
//                .build();
//    }
//}
