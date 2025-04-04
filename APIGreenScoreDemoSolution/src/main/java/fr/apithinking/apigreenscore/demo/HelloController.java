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
    private static final String URL_ERROR_MESSAGE = "L'URL doit commencer par http:// ou https://";
    private static final String JSON_MIME_TYPE = "application/json";
    private static final String XML_MIME_TYPE = "application/xml";

    private final LogService logService;

    @Autowired
    public HelloController(LogService logService) {
        this.logService = logService;
    }

    /**
     * Endpoint to return a greeting message.
     *
     * @param url the URL parameter
     * @param request the HTTP request
     * @return a JSON response with a greeting message
     */
    @GetMapping("/hello")
    public ResponseEntity<String> hello(@RequestParam(value = "url", required = false, defaultValue = "defaultUrl") String url,
                                        HttpServletRequest request) {

        long start = System.currentTimeMillis();
        String responseBody = "{\"message\": \"Hello, API Green Score! URL: " + url + "\"";

        long end = System.currentTimeMillis();

        long responseTime = end - start;
        responseBody += ", \"responseTime\": " + responseTime + ", \"size\": " + responseBody.getBytes().length + "}";
        logService.logApiCall(
                url,
                request.getRemoteAddr(),
                responseTime,
                responseBody.getBytes().length
        );

        return ResponseEntity.ok()
                .header("Content-Type", JSON_MIME_TYPE)
                .body(responseBody);
    }

    /**
     * Endpoint to test an external URL.
     *
     * @param url the URL to test
     * @param request the HTTP request
     * @return a response indicating the result of the URL test
     */
    @GetMapping("/url2test")
    public ResponseEntity<String> testExternalUrl(@RequestParam String url, HttpServletRequest request) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return ResponseEntity.badRequest().body(URL_ERROR_MESSAGE);
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

    /**
     * Endpoint to test a JSON URL.
     *
     * @param url the URL to test
     * @param request the HTTP request
     * @return a response indicating the result of the JSON URL test
     */
    @GetMapping("/json2test")
    public ResponseEntity<String> testJsonUrl(@RequestParam String url, HttpServletRequest request) {
        return fetchAndLogExternalUrl(url, request, JSON_MIME_TYPE);
    }

    /**
     * Endpoint to test an XML URL.
     *
     * @param url the URL to test
     * @param request the HTTP request
     * @return a response indicating the result of the XML URL test
     */
    @GetMapping("/xml2test")
    public ResponseEntity<String> testXmlUrl(@RequestParam String url, HttpServletRequest request) {
        return fetchAndLogExternalUrl(url, request, XML_MIME_TYPE);
    }
    /**
     * Fetches and logs an external URL.
     *
     * @param url the URL to fetch
     * @param request the HTTP request
     * @param expectedContentType the expected content type of the response
     * @return a response indicating the result of the URL fetch
     */
    private ResponseEntity<String> fetchAndLogExternalUrl(String url, HttpServletRequest request, String expectedContentType) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return ResponseEntity.badRequest().body(URL_ERROR_MESSAGE);
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
        String contentType = response.getHeaders().getContentType() != null ? response.getHeaders().getContentType().toString() : "";

        // Vérifier le type MIME
        if (!contentType.contains(expectedContentType)) {
            return ResponseEntity.badRequest().body("⚠ Le contenu retourné n'est pas du type attendu (" + expectedContentType + "). Type reçu : " + contentType);
        }

        logService.logApiCall(url, request.getRemoteAddr(), responseTime, payloadSize, statusCode);

        return ResponseEntity.ok("✅ " + expectedContentType.toUpperCase() + " reçu. Statut: " + statusCode + " / Temps: " + responseTime + " ms / Taille: " + payloadSize + " octets");
    }
    /**
     * Endpoint to convert JSON to XML.
     *
     * @param url the URL to fetch JSON from
     * @param request the HTTP request
     * @return a response with the converted XML
     */
    @GetMapping("/json2xml")
    public ResponseEntity<String> convertJsonToXml(@RequestParam String url, HttpServletRequest request) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return ResponseEntity.badRequest().body(URL_ERROR_MESSAGE);
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
        int statusCode = response.getStatusCode().value();
        int payloadSize = response.getBody() != null ? response.getBody().getBytes().length : 0;

        logService.logApiCall(url, request.getRemoteAddr(), responseTime, payloadSize, statusCode);

        String json = response.getBody();

        // ✅ Convertir JSON en XML
        try {
            org.json.JSONObject jsonObject = new org.json.JSONObject(json);
            String xml = org.json.XML.toString(jsonObject);

            // Retourne une réponse XML
            return ResponseEntity.ok()
                    .header("Content-Type", XML_MIME_TYPE)
                    .body(xml);

        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Erreur de conversion JSON → XML : " + ex.getMessage());
        }
    }

}
