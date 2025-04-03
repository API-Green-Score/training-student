package fr.apithinking.apigreenscore.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);
    private final LogEntryRepository repository;

    public LogService(LogEntryRepository repository) {
        this.repository = repository;
    }
    public void logApiCall(String url, String ip, long responseTime, int payloadSize) {
        logApiCall(url, ip, responseTime, payloadSize, 200);
    }

    public void logApiCall(String url, String ip, long responseTime, int payloadSize, int statusCode) {
        LogEntry entry = new LogEntry();
        entry.setUrl(url);
        entry.setTimestamp(System.currentTimeMillis());
        entry.setCallerIp(ip);
        entry.setResponseTime(responseTime);
        entry.setPayloadSize(payloadSize);
        entry.setStatusCode(statusCode);
        repository.save(entry);
        logger.info("✅ Log API externe → IP: {}, URL: {}, Statut: {}, Durée: {} ms, Payload: {} octets",
                ip, url, statusCode, responseTime, payloadSize);

        System.out.println("📝 LogEntry enregistré : " + entry);
    }
}
