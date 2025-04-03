package fr.apithinking.apigreenscore.demo;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);
    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
        System.out.println("LogController initialisé");
    }

    // Ce endpoint peut être utilisé pour tester manuellement la logique de log
    @GetMapping
    public ResponseEntity<String> log(
            @RequestParam(value = "url", required = false, defaultValue = "non fournie") String url,
            HttpServletRequest request
    ) {
        long timestamp = System.currentTimeMillis();
        String ip = request.getRemoteAddr();

        LogEntry entry = new LogEntry();
        entry.setUrl(url);
        entry.setTimestamp(timestamp);
        entry.setStatusCode(200); // ici, tu peux mettre dynamiquement selon le contexte réel
        entry.setPayloadSize(0); // ou la taille de la réponse, si tu veux la mesurer
        entry.setResponseTime(0); // à calculer si besoin
        entry.setCallerIp(ip);

        logger.debug("Logging API call from IP: {}", ip);


        logService.logApiCall(url, ip, entry.getResponseTime(), entry.getPayloadSize(), entry.getStatusCode());
        System.out.println("Log enregistré : " + entry);

        return ResponseEntity.ok("Appel loggé avec succès");
    }
}
