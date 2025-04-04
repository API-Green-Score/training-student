# TP Numérique Responsable : Réponses aux exerices

Voici un ensemble de propositions pour réaliser chaque exercice pas à pas, avec des explications détaillées. Les
exemples de code sont fournis à titre d’illustration et peuvent être adaptés selon vos besoins et votre environnement.

*Le temps global de ce TP est estimé à **1h45**, mais il peut varier en fonction de votre niveau de connaissance et de votre expérience* 

---
## Exercice 1 : Création d’un système de logs pour suivre les appels API 
⏱️ temps : 25 min

### Objectif

Concevoir un système de logs efficace pour surveiller l’utilisation des API :

* Enregistrer chaque appel (URL, timestamp, taille du payload, temps de réponse, etc.).
* Pouvoir analyser facilement par la suite l’impact des différentes optimisations appliquées.

### Approche et explication

1. **Choisir le support de stockage** :

   * Vous pouvez enregistrer les logs dans un fichier JSON, dans un fichier plat (log4j2, logback) ou les centraliser dans
     une base de données (par ex. H2 en mémoire pour la démo).
   * Une base de données permet des requêtes plus flexibles (recherches, filtres, etc.), tandis qu’un fichier plat est plus
     léger à mettre en place.

2. **Mettre en place la structure de log** :
   * Définissez un modèle (LogEntry) qui représentera chaque entrée de log :

**Exemple de LogEntry**
```java
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
```

3. **Concevoir un contrôleur ou un service dédié** :

   * Ce service (ou ce contrôleur) va intercepter ou recueillir les données d’appel (URL, temps de début, temps de fin,
     taille du payload, code de statut HTTP, etc.).
   * Si vous utilisez Spring Boot avec un RestTemplate, vous pouvez mesurer le temps avant et après l’appel pour déterminer
     la durée.

**Exemple d'utilisation dans un contrôleur**

```java
@GetMapping("/hello")
public ResponseEntity<String> hello(@RequestParam String url, HttpServletRequest request) {
long start = System.currentTimeMillis();
String responseBody = "{\"message\": \"Hello, API Green Score! URL: " + url + "\"";
long end = System.currentTimeMillis();

    long responseTime = end - start;
    responseBody += ", \"responseTime\": " + responseTime + ", \"size\": " + responseBody.getBytes().length + "}";

    logService.logApiCall(url, request.getRemoteAddr(), responseTime, responseBody.getBytes().length);

    return ResponseEntity.ok()
        .header("Content-Type", "application/json")
        .body(responseBody);
}
```
4. **Persister ou écrire les logs** :

   * Si base H2 : créer un repository Spring Data JPA (LogRepository) et persister l’objet LogEntry.
   * Sinon, utilisez l’API de logging (SLF4J, Log4J, Logback) pour écrire les informations (en format JSON si besoin) dans
     un fichier.

5. **Structure finale** :

   * Un service/contrôleur qui fait l’appel API.
   * Un repository (ou le logger) pour stocker les infos.

### Exemple de code (version améliorée)

#### Classe de configuration pour H2 (optionnel)

``` java

// application.properties
spring.datasource.url=jdbc:h2:mem:logdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
logging.level.org.hibernate.SQL=DEBUG
```

#### Entité JPA

``` Java
@Entity
public class LogEntry {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String url;
  private long timestamp;
  private int payloadSize;
  private long responseTime;
  private int statusCode;
  
  // Constructeurs, getters, setters
}
```
#### Repository

``` Java
public interface LogRepository extends JpaRepository<LogEntry, Long> {
}
```

#### Contrôleur pour logger les appels

``` Java
@RestController
@RequestMapping("/logs")
public class LogController {

  private final Logger logger = LoggerFactory.getLogger(LogController.class);
  private final LogRepository logRepository;
  
  public LogController(LogRepository logRepository) {
    this.logRepository = logRepository;
  }
  
  @GetMapping("/log")
  public ResponseEntity<String> logApiCall(@RequestParam String url) {
      long startTime = System.currentTimeMillis();
      RestTemplate restTemplate = new RestTemplate();
    
      // Effectuer la requête
      ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    
      // Calcul de la durée et de la taille
      long duration = System.currentTimeMillis() - startTime;
      int payloadSize = response.getBody() != null ? response.getBody().getBytes().length : 0;
      int statusCode = response.getStatusCodeValue();
    
      // Construction de notre LogEntry
      LogEntry logEntry = new LogEntry();
      logEntry.setUrl(url);
      logEntry.setTimestamp(System.currentTimeMillis());
      logEntry.setPayloadSize(payloadSize);
      logEntry.setResponseTime(duration);
      logEntry.setStatusCode(statusCode);
    
      // Sauvegarde dans la base
      logRepository.save(logEntry);
    
      // Log « classique »
      logger.info("URL: {} | Status: {} | Payload: {} bytes | Response Time: {} ms",
      url, statusCode, payloadSize, duration);
    
      return ResponseEntity.ok("Log ajouté");
  }
}
``` 
### Résultat attendu
* Un service Spring Boot qui logge chaque appel API.
* Les logs sont disponibles soit dans la base H2 (consultez `http://localhost:8080/h2-console` pour voir la table) soit dans
un fichier via Logback/Log4j.
* Vous pouvez ainsi analyser plus tard la taille des payloads, le temps de réponse, etc.

---
## Exercice 2 : Choix du format d’échange de données et impact environnemental
⏱️ temps : 20 min

### Objectif

* Comparer l’utilisation du JSON et du XML pour le même appel d’API afin de mesurer la différence de taille du payload.
* Vérifier l’impact sur le réseau et le stockage, surtout si on extrapole à un volume important (1 million d’appels par
jour).

### Approche et explication

**1. OpenLibrary API :**
* On effectue une requête sur `https://openlibrary.org/search.json?q=harry+potter` pour récupérer des données en JSON.
* On utilise un convertisseur en ligne (ou un outil local) pour transformer la réponse JSON en XML.

**2. Mesure de la taille :**
* Via Postman, on peut lire la taille du payload retourné (en-têtes ou section « Size » de la réponse).
* On peut également copier la réponse brute dans un éditeur de texte pour compter le nombre d’octets/caractères.

**3. Comparaison JSON vs XML :**
* Généralement, le JSON est plus léger que le XML (pas de balises de fermeture redondantes).
* On logge les résultats dans notre système de logs : par exemple, en enregistrant dans la DB H2 (ou dans un fichier) la
taille du JSON et celle du XML pour la même requête.

**4. Analyse environnementale :**
* Si l’on multiplie la différence de taille par 1 million d’appels, on voit la différence de volume transféré sur le
réseau (et donc la consommation d’énergie associée).
* Si JSON est par exemple 10% plus léger qu’un XML équivalent, sur 1 million d’appels, le gain est conséquent.

### Exemple de mesure
* Réponse JSON (OpenLibrary) : ~40 Ko (exemple hypothétique).
* Conversion en XML : ~55 Ko (exemple hypothétique).
* Différence : 15 Ko. Sur 1 million d’appels, cela représente 15 Go de données supplémentaires, ce qui a un impact non
négligeable en termes de bande passante et de stockage.

### Résultat attendu
* **Format le plus léger** : JSON ressort généralement gagnant.
* **Usage des logs** : Les logs montrent la taille des payloads.
* **Documentation Green Score** :
  * Mentionner qu’une des bonnes pratiques est de choisir un format de données léger pour réduire l’empreinte carbone (
  réduction du trafic réseau, etc.).
  * Proposer d’utiliser la serialisation JSON par défaut pour les nouveaux endpoints.
  * Indiquer cette pratique dans le repo GitHub dans un fichier GREEN_GUIDELINES.md, par exemple.


---
## Exercice 3 : Réduction des données transférées avec Java Spring Boot 
⏱️ temps : 25 min

### Objectif
* Mettre en place des bonnes pratiques d’optimisation : pagination, filtrage, voire compression.
* Montrer que le volume de données effectivement renvoyé au client est réduit, vérifié via les logs.

### Approche et explication

**1.Pagination :**
Permettre au client de ne pas tout récupérer en une fois. Par exemple, on renvoie 10 résultats par page, ou un nombre
paramétrable.

**2.Filtrage :**
* Si on a un gros objet (ex. : un objet Address plus complet), donner la possibilité au client de spécifier les champs
qu’il souhaite ou de filtrer par code postal, par pays, etc.

**3. Compression (optionnelle) :**
* Spring Boot peut activer la compression GZIP des réponses HTTP via la configuration (dans application.properties par
exemple), ce qui réduit encore la taille des payloads transférés.

### Exemple de code (pagination et filtrage simples)

``` Java
@RestController
@RequestMapping("/addresses")
public class AddressController {

  // Exemple de mock
  private static final List<String> addresses = List.of(
    "10 avenue des Champs-Élysées, Paris",
    "5 rue de la Paix, Lyon",
    "12 boulevard Haussmann, Marseille",
    "50 rue Nationale, Bordeaux",
    "15 boulevard de Sébastopol, Paris",
    "23 rue Victor Hugo, Lyon"
    // etc.
  );
  
  // Pagination simple
  @GetMapping
  public ResponseEntity<List<String>> getAddresses(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "2") int size,
    @RequestParam(required = false) String city // Filtre éventuel
  ) {
  
    // Filtrage simple (optionnel)
    Stream<String> stream = addresses.stream();
    if (city != null && !city.isEmpty()) {
      stream = stream.filter(addr -> addr.toLowerCase().contains(city.toLowerCase()));
    }
    
    // Pagination
    List<String> filtered = stream.collect(Collectors.toList());
    int start = page * size;
    int end = Math.min(start + size, filtered.size());
    if (start > filtered.size()) {
      return ResponseEntity.ok(Collections.emptyList());
    }
    List<String> paginatedResult = filtered.subList(start, end);
    
    // On peut éventuellement logger la taille renvoyée
    // ...
    return ResponseEntity.ok(paginatedResult);
  }
}
``` 
* **city** : exemple de paramètre de filtrage (pour ne renvoyer que les adresses contenant “Paris” ou “Lyon”, etc.).
* **page et size** : paramètres de pagination.

### Vérification via les logs
* Dans le code, on peut logger la taille du résultat renvoyé (nombre d’éléments, poids total si converti en JSON, etc.).
* On pourra ensuite observer dans la base H2 ou dans les logs la différence de payload renvoyé avant/après
filtrage/pagination.

### Résultat attendu
* On obtient une API REST maîtrisant le volume de données renvoyées.
* Les logs démontrent la réduction significative de la taille du payload grâce à la pagination et au filtrage.

---
## Exercice 4 : Utilisation du cache pour limiter les requêtes inutiles [Facultatif]
⏱️ temps : 20 min

### Objectif
* Réduire l’empreinte réseau en mettant en place un cache qui évite de redemander plusieurs fois la même information à une
API externe.

### Approche et explication
**1. Spring Cache :** 

On active le cache via l’annotation `@EnableCaching` dans la classe de configuration principale de Spring Boot.
On utilise l’annotation `@Cacheable` sur la méthode qui fait l’appel à l’API externe.

**2. Exemple :**

* On fait un appel à l’API Spotify pour récupérer des catégories de musique.
* Lorsque la méthode est appelée plusieurs fois, la première fois fait effectivement l’appel réseau, puis les appels
suivants renvoient la donnée en cache tant que le TTL (time to live) ou la politique d’expiration ne sont pas dépassés.

### Exemple de code
``` Java
@SpringBootApplication
@EnableCaching
public class MusicApplication {
  public static void main(String[] args) {
    SpringApplication.run(MusicApplication.class, args);
  }
}

@Service
public class MusicService {

private final RestTemplate restTemplate = new RestTemplate();

  @Cacheable("categories")
  public String getCategories() {
    String url = "https://api.spotify.com/v1/browse/categories";
    // Dans la vraie vie, il faudrait ajouter un token OAuth, etc.
    return restTemplate.getForObject(url, String.class);
  }
}

@RestController
@RequestMapping("/music")
public class MusicController {
  private final MusicService musicService;
  public MusicController(MusicService musicService) {
    this.musicService = musicService;
  }

  @GetMapping("/categories")
  public ResponseEntity<String> getMusicCategories() {
    String categories = musicService.getCategories();
    return ResponseEntity.ok(categories);
  }
}

```

### Vérification via les logs
* Avant d’activer le cache : l’appel à getCategories() déclenche une requête sur Spotify à chaque fois.
* Après activation du cache : la première requête seulement fait l’appel à Spotify, les suivantes sont servies depuis le
cache (jusqu’à invalidation ou TTL).
* Les logs/entrées en base (temps de réponse, URL appelée, …) permettent de voir qu’on ne fait plus X appels, seulement 1.

### Résultat attendu
* **Réduction drastique des appels API externes** et donc de la bande passante consommée.
* Gains environnementaux et performance améliorée.

---
## 5. Conclusion & Contribution Open Source (15 min)

### Synthèse des résultats

**1. Impact des logs :**
  * Mise en place d’un système de logs donne de la visibilité sur les métriques clés (temps de réponse, taille de payload,
fréquence d’appels).
  * Permet de quantifier précisément les gains des optimisations (pagination, filtrage, cache, etc.).

**2.Comparaison JSON vs XML :**
* Le JSON se révèle plus léger que le XML dans la plupart des cas.
* Sur un volume important (1 million d’appels/jour), la réduction de bande passante est conséquente.

**3. Pagination, filtrage, compression :**
* Réduction du volume de données transférées en ne renvoyant que les informations utiles.
* Journaux (logs) confirment la baisse de la taille des payloads.

**4.Cache :**
* Réduit le nombre de requêtes vers les services tiers.
* Gains mesurables en temps de réponse et en utilisation du réseau.

### Contributions à API Green Score

* **Documentation :**
  * Rédiger un document (ex. `GREEN_GUIDELINES.md` ou wiki) dans le repo qui explique chacune de ces optimisations :

    * Choix d’un format léger (JSON).
    * Limiter le volume de données via pagination/filtrage.
    * Mise en cache des réponses récurrentes.
    * Mesure et suivi via des logs détaillés pour prouver l’efficacité.

* **Outils de mesure :**
  * Exemples concrets de logs et scripts de requêtage sur la base de données H2 pour suivre l’évolution des temps de réponse
  et la taille moyenne des payloads.

* **Accessibilité pour les nouveaux contributeurs :**
  * Expliquer dans un README comment démarrer le projet, configurer la base H2, exécuter des tests de performance, etc.

En appliquant l’ensemble de ces approches, on améliore à la fois les performances de l’application et on réduit son
impact environnemental, ce qui correspond parfaitement aux objectifs d’un projet respectant un « Green Score ».


Voici deux exemples de règles (en anglais et en français), rédigées dans un style cohérent avec le format de
[API-Green-Score.](https://github.com/API-Green-Score/APIGreenScore/blob/main/resources/YYxx_en.md)


**Note** : Les identifiants (YYxx_en.md / YYxx_fr.md) sont purement illustratifs. Adaptez-les en fonction de l’organisation
de votre dépôt et de la nomenclature déjà existante.

## 1. Exemple de règle : Utiliser JSON plutôt que XML

Fichier `01xx_en.md` (version anglaise)

``` markdown
# 01xx - Prefer JSON over XML for Data Exchange

## Summary

Using JSON instead of XML for API responses generally reduces payload size and leads to lower bandwidth consumption and
faster response times.

## Context & Explanation

- **Data format choice** has a significant impact on the size of the payload returned by an API.
- **JSON** tends to be more compact than **XML** because it has less structural overhead (no closing tags).
- A smaller payload reduces both **network usage** and **latency**, improving performance and reducing energy
  consumption.

## Recommendations

1. **Default to JSON** when designing new APIs or endpoints.
2. **Measure payload size** for all responses, comparing JSON vs. XML when deciding the format for existing APIs.
3. **Use compression** (such as GZIP) whenever possible to further reduce payload size.

## Good Practices

- **Log payload sizes** in your monitoring system to track improvements over time.
- Provide a fallback or **content negotiation** if some consumers still require XML.

## Anti-patterns

- Sending both JSON and XML simultaneously in the same response.
- Using verbose XML tags without any strict need for an XML schema.

## Green Impact

- Lower bandwidth usage translates into **less energy consumption** on the server and client side.
- Helps reduce carbon footprint for large-scale services (especially when dealing with millions of requests per day).

## References

- [JSON vs. XML Benchmark Studies](https://www.json.org/json-en.html)
- [Green Software Foundation Patterns](https://greensoftware.foundation/)
```

Fichier `01xx_fr.md` (version française)
``` markdown
# 01xx - Privilégier JSON à XML pour l’échange de données

## Résumé

L’utilisation de JSON au lieu de XML pour les réponses d’API réduit généralement la taille du payload et entraîne une
consommation de bande passante plus faible, ainsi qu’un temps de réponse plus rapide.

## Contexte & Explications

- Le **choix du format de données** a un impact significatif sur la taille du contenu renvoyé par une API.
- **JSON** est souvent plus compact que **XML** car il contient moins de balises et de métadonnées.
- Un payload plus petit réduit à la fois la **charge réseau** et la **latence**, améliorant ainsi les performances et
  réduisant la consommation d’énergie.

## Recommandations

1. **Utiliser JSON par défaut** lors de la conception de nouvelles API ou de nouveaux endpoints.
2. **Mesurer la taille des payloads** pour toutes les réponses, en comparant JSON et XML pour les API existantes.
3. **Activer la compression** (ex. GZIP) autant que possible pour réduire davantage la taille des données transférées.

## Bonnes pratiques

- **Logger la taille des payloads** afin de suivre l’évolution et les gains obtenus au fil du temps.
- Offrir un mécanisme de **négociation de contenu** pour les consommateurs ayant absolument besoin de XML.

## Anti-patterns

- Envoyer JSON et XML simultanément dans la même réponse.
- Utiliser des balises XML verbeuses sans nécessité stricte (schéma XML complexe, validation, etc.).

## Impact Green

- Une bande passante réduite implique **moins de consommation énergétique** côté serveur et côté client.
- Contribue à diminuer l’empreinte carbone pour les services à grande échelle (notamment avec des millions d’appels
  quotidiens).

## Références

- [Étude comparative JSON vs XML](https://www.json.org/json-fr.html)
- [Green Software Foundation Patterns](https://greensoftware.foundation/)
```
## 2. Exemple de règle : Mettre en place un cache pour limiter les requêtes inutiles

Fichier `02xx_en.md` (version anglaise)

``` markdown
# 02xx - Implement Caching for Repeated Requests

## Summary

Caching frequently accessed data (or results of API calls) reduces the number of requests sent to external services,
lowers bandwidth usage, and improves response times.

## Context & Explanation

- Services making **repeated calls** to the same endpoint consume extra network resources and increase the carbon
  footprint.
- **Caching** (in-memory or distributed) can store previously retrieved data for a specified duration (TTL).
- Less network traffic translates to energy savings and better performance.

## Recommendations

1. **Enable Spring Cache** (or a similar mechanism) on frequently accessed API endpoints.
2. Select an **appropriate TTL** (time to live) to avoid stale data while still minimizing calls.
3. Monitor **cache hit ratio** to measure effectiveness.

## Good Practices

- Use **@Cacheable** annotations in Spring Boot or an equivalent caching library.
- Log **cache hits/misses** to track how often data is served from cache vs. network calls.

## Anti-patterns

- Caching data that changes very frequently, causing stale data issues.
- Not invalidating or refreshing the cache when the underlying data is updated.

## Green Impact

- Fewer external API calls reduces **server workloads** and **network data transfer**.
- Particularly beneficial for large user bases or APIs with high read-to-write ratios.

## References

- [Spring Boot Caching Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#io.caching)
- [Green Patterns for Microservices](https://greensoftware.foundation/)
```
Fichier `02xx_fr.md` (version française)
``` markdown

# 02xx - Mettre en place un système de cache pour limiter les requêtes répétées

## Résumé

Le cache des données fréquemment consultées (ou des résultats d’appels API) permet de réduire le nombre de requêtes
envoyées aux services externes, de diminuer la bande passante utilisée et d’améliorer les temps de réponse.

## Contexte & Explications

- Les services effectuant des **appels répétés** vers le même endpoint consomment des ressources réseau supplémentaires
  et augmentent l’empreinte carbone.
- Le **caching** (en mémoire ou distribué) permet de stocker temporairement des données récupérées, avec une durée de
  vie (TTL).
- Moins de trafic réseau se traduit par des économies d’énergie et une performance accrue.

## Recommandations

1. **Activer Spring Cache** (ou un mécanisme similaire) sur les endpoints fréquemment utilisés.
2. Définir un **TTL** (time to live) adapté pour éviter les données obsolètes, tout en minimisant le nombre d’appels.
3. Surveiller le **taux de cache hit** pour évaluer son efficacité.

## Bonnes pratiques

- Utiliser les annotations **@Cacheable** de Spring Boot ou une bibliothèque de cache équivalente.
- Logger les **hits et misses du cache** pour déterminer la fréquence à laquelle les données sont réellement servies
  depuis le cache par rapport aux appels réseau.

## Anti-patterns

- Mettre en cache des données qui changent très souvent, causant des problèmes de données obsolètes.
- Ne pas invalider ou rafraîchir le cache lorsque les données sous-jacentes sont mises à jour.

## Impact Green

- Réduire le nombre d’appels externes diminue la **charge sur les serveurs** et le **volume de données transféré**.
- Particulièrement intéressant pour les services avec de nombreux utilisateurs ou un fort ratio de lecture par rapport à
  l’écriture.

## Références

- [Documentation sur le Caching dans Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#io.caching)
- [Green Patterns pour les Microservices](https://greensoftware.foundation/)
```
## Comment intégrer ces exemples dans votre repo

**1.Nomenclature de fichiers :**
* Placez vos règles dans le dossier resources/, en respectant un nom de fichier du type `01xx_en.md` et `01xx_fr.md`, où `01xx`
correspond à l’ID de la règle (à adapter selon votre convention).

**2.Sommaire/Index :**
* Assurez-vous qu’il y a un fichier d’index (par ex. `RULES_INDEX.md`) ou un README qui recense vos règles et fournit un
lien vers chacun des fichiers `_en.md` et `_fr.md`.

**3.Pull Request :**
* Proposez vos nouvelles règles ou modifications via une Pull Request sur le dépôt [API-Green-Score](https://github.com/API-Green-Score/APIGreenScore).
* Discutez avec les mainteneurs pour valider la rédaction et l’alignement avec les bonnes pratiques existantes.

De cette manière, vous obtenez des règles bilingues, structurées et faciles à maintenir, tout en contribuant à la
démarche « Green Score » pour des APIs plus responsables.