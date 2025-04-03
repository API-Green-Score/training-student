package fr.apithinking.apigreenscore.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private long timestamp;
    private int payloadSize;
    private long responseTime;
    private int statusCode;
    private String callerIp;

    @Override
    public String toString() {
        return "LogEntry{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", timestamp=" + timestamp +
                ", payloadSize=" + payloadSize +
                ", responseTime=" + responseTime +
                ", statusCode=" + statusCode +
                ", callerIP=" + callerIp +
                '}';
    }
}
