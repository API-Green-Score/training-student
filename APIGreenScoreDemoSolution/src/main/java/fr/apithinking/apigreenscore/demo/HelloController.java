package fr.apithinking.apigreenscore.demo;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class HelloController {

    private final LogService logService;

    @Autowired
    public HelloController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "url", required = false, defaultValue = "defaultUrl") String url,
                        HttpServletRequest request) {

        long start = System.currentTimeMillis();
        String responseBody = "Hello, API Green Score! URL: " + url;
        long end = System.currentTimeMillis();

        logService.logApiCall(
                url,
                request.getRemoteAddr(),
                end - start,
                responseBody.getBytes().length
        );

        return responseBody;
    }

    @GetMapping("/url2test")
    public ResponseEntity<String> testExternalUrl(@RequestParam String url, HttpServletRequest request) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return ResponseEntity.badRequest().body("L'URL doit commencer par http:// ou https://");
        }

        RestTemplate restTemplate = new RestTemplate();
        long start = System.currentTimeMillis();
        ResponseEntity<String> response;

        try {
            response = restTemplate.getForEntity(url, String.class);
        } catch (Exception e) {
            logService.logApiCall(url, request.getRemoteAddr(), System.currentTimeMillis() - start, 0, 500);
            return ResponseEntity.internalServerError().body("Erreur lors de l'appel de l'URL : " + e.getMessage());
        }

        long responseTime = System.currentTimeMillis() - start;
        int payloadSize = response.getBody() != null ? response.getBody().getBytes().length : 0;
        int statusCode = response.getStatusCode().value();

        logService.logApiCall(url, request.getRemoteAddr(), responseTime, payloadSize, statusCode);

        return ResponseEntity.ok("Appel réussi. Statut: " + statusCode + " / Temps: " + responseTime + " ms");
    }
}
