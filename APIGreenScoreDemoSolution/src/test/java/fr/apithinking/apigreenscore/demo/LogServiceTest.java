package fr.apithinking.apigreenscore.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LogServiceTest {

    @InjectMocks
    private LogService logService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogApiCall() {
        String url = "http://example.com";
        String remoteAddr = "127.0.0.1";
        long responseTime = 100;
        int payloadSize = 200;
        int statusCode = 200;

        logService.logApiCall(url, remoteAddr, responseTime, payloadSize, statusCode);

        // Verify that the log method was called with the correct parameters
        verify(logService, times(1)).logApiCall(url, remoteAddr, responseTime, payloadSize, statusCode);
    }
}