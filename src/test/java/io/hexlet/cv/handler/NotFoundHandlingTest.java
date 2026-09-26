package io.hexlet.cv.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.hexlet.cv.audit.AuditEventType;
import io.hexlet.cv.audit.support.AuditLogCaptureSupport;
import io.hexlet.cv.service.PageSectionService;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotFoundHandlingTest extends AuditLogCaptureSupport {

    private static final String UNKNOWN_PATH = "/api/not-the-path-you-are-looking-for";
    private static final String FAILURE_DETAIL = "internal-detail-iddqd";

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private PageSectionService pageSectionService;

    private Logger handlerLogger;
    private ListAppender<ILoggingEvent> handlerAppender;

    @Override
    protected List<String> secretMarkers() {
        return List.of(FAILURE_DETAIL);
    }

    @BeforeEach
    void attachHandlerAppender() {
        handlerAppender = new ListAppender<>();
        handlerAppender.start();

        handlerLogger = (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);
        handlerLogger.addAppender(handlerAppender);
    }

    @AfterEach
    void detachHandlerAppender() {
        handlerLogger.detachAppender(handlerAppender);
        handlerAppender.stop();
    }

    @Test
    void shouldReturnNotFoundForUnknownPath() throws Exception {
        mockMvc.perform(get(UNKNOWN_PATH))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotWriteAuditEventForUnknownPath() throws Exception {
        mockMvc.perform(get(UNKNOWN_PATH));

        assertThat(capturedLines())
                .as("скан несуществующих путей не должен засорять журнал аудита")
                .isEmpty();
    }

    @Test
    void shouldNotLogErrorForUnknownPath() throws Exception {
        mockMvc.perform(get(UNKNOWN_PATH));

        assertThat(handlerAppender.list)
                .as("отсутствие ресурса не ошибка сервера, стек в error-логе прячет настоящие поломки")
                .noneMatch(event -> event.getLevel() == Level.ERROR);
    }

    @Test
    void shouldStillReportServerErrorForRealFailure() throws Exception {
        doThrow(new IllegalStateException(FAILURE_DETAIL))
                .when(pageSectionService).findAllOnPage(anyString(), any());

        mockMvc.perform(get("/"))
                .andExpect(status().isInternalServerError());

        assertThat(handlerAppender.list)
                .as("настоящая поломка обязана остаться видимой в error-логе")
                .anyMatch(event -> event.getLevel() == Level.ERROR);

        assertThat(capturedLines())
                .as("настоящая поломка обязана остаться в журнале аудита")
                .anyMatch(line -> line.contains(AuditEventType.UNHANDLED_ERROR.name()));
    }
}
