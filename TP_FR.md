# TP Numérique Responsable

## 📌 Objectifs du TP
1. **Contribuer à un projet Open Source** – Comprendre l’évaluation de l’impact environnemental des API et proposer des améliorations.
2. **Sensibiliser à l’impact environnemental du numérique** – Identifier les bonnes pratiques et les appliquer sur des cas concrets.
3. **Créer des logs pertinents** – Enregistrer et analyser les appels API avec Java/Spring Boot pour améliorer la compréhension et l’optimisation des services.
4. **Développer des API optimisées en Java Spring Boot** – Implémenter et tester de bonnes pratiques d’éco-conception.
5. **Documenter et enrichir un projet Open Source** – Contribuer au **repo GitHub API Green Score** pour aider les futurs utilisateurs.

*Le temps global de ce TP est estimé à **1h45**, mais il peut varier en fonction de votre niveau de connaissance et de votre expérience* 

---
## Plan du TP (2 heures)

### 1. Introduction et mise en place des outils 
⏱️ temps : 20 min
#### 📌 Objectifs :
* Comprendre l’impact environnemental des API.
* Découvrir les **outils du TP** : GitHub (API Green Score), Postman (tests API), Java Spring Boot pour le développement et l’analyse.

#### ✅ Tâches :
1.  Présentation du **numérique responsable** et de l’impact environnemental des API.
2. Introduction à l’**API Green Score** (7 domaines d’évaluation).
3. Explication des outils :
   * Postman : test et validation des payloads.
   * GitHub : contribution Open Source.
   * Java Spring Boot : implémentation des API.

4. **Installation et configuration des outils** (Postman, IDE pour Java, GitHub).

⚠️ **Consignes :**
* L’utilisation de LLM (IA Generative) est possible pour aider à débloquer mais vous devez **comprendre** la démarche et l’objectif
* Pas de challenge pour avoir fini les exercices le plus vite possible, mais bonus si contribution au repo Github sous forme de Pull Resquest non généré par une IA
* Les étudiant.e.s avancent à leur rythme
---

## Séquence d’exercices 

⏱️ temps : 1h30

**Les exercices s’enchaînent de manière logique, avec une collecte de logs dès le départ pour analyser les impacts des optimisations.**

### Exercice 1 : Création d’un système de logs pour suivre les appels API

⏱️ temps : 25mn

📌 **Objectif** : Concevoir un système de logs efficace pour surveiller l’utilisation des API.

📜 **Scénario** : Votre entreprise veut analyser les requêtes API pour comprendre leur impact et améliorer leur efficacité.

✅ **Tâches** :

1. **Développer un service de log en Java Spring Boot** qui :
   * Enregistre chaque appel API (URL, timestamp, taille du payload, temps de réponse).
   * Ajoute un niveau de détail suffisant pour suivre l’évolution des optimisations.
   * Regroupe les logs dans un fichier JSON ou une base de données H2.

💻 **Exemple de code pour générer les logs en Java Spring Boot :**

_(Les étudiants doivent ensuite modifier et améliorer ce code)_

```java
@RestController
@RequestMapping("/logs")
public class LogController {

    private final Logger logger = LoggerFactory.getLogger(LogController.class);

    @GetMapping("/log")
    public ResponseEntity<String> logApiCall(@RequestParam String url) {
        long startTime = System.currentTimeMillis();
        
        RestTemplate restTemplate = new RestTemplate();
            
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
        long duration = System.currentTimeMillis() - startTime;
        int payloadSize = response.getBody().getBytes().length;
        
        logger.info("URL: {} | Status: {} | Payload: {} bytes | Response Time: {} ms", url, response.getStatusCode(), payloadSize, duration);
        
        return ResponseEntity.ok("Log ajouté");
    }
}
```
📌 **Résultat attendu :**

* Un fichier ou une base de données contenant l’historique des appels API.
* Une **meilleure compréhension de l’impact des requêtes** et des gains des optimisations.

---
### Exercice 2 : Choix du format d’échange de données et impact environnemental 

⏱️ temps : 20mn

📌 **Objectif** : Comparer JSON et XML pour identifier l’impact environnemental.

🔧 **API utilisée** : OpenLibrary API

📜 **Scénario** : Vous travaillez sur une application de bibliothèque en ligne et devez choisir un format de données optimisé.

✅ **Tâches :**

1. Effectuer une requête sur OpenLibrary en JSON et en XML via Postman.
   * `https://openlibrary.org/search.json?q=harry+potter`
   * Convertir au format XML `https://www.site24x7.com/fr/tools/xml-en-json.html`

2. Vérifier la **taille des payloads** avec Postman ou tout autre outil de votre choix et les **enregistrer dans le système de logs.**
3. Comparer les tailles des fichiers JSON et XML et analyser les logs générés.
4. Déduire l’impact en termes de **bande passante** et **stockage**.
5. faire cette comparaison avec un volume d’appel de 1 millions d’appels par jour à l’API

📌 **Résultat attendu :**

* Quel est le formation le plus léger → impact réduit sur le réseau et stockage.
* Utilisation des logs pour quantifier l’optimisation.
* Documentation sur la **règle API Green Score concernée** et suggestion d’amélioration dans le repo GitHub.

---
### Exercice 3 : Réduction des données transférées avec Java Spring Boot 

⏱️ temps : 25mn

📌 **Objectif** : Implémenter des **bonnes pratiques d’optimisation** (pagination, filtrage, compression).

📜 **Scénario** : Vous développez un service d’adresse et devez optimiser la transmission des données.

✅ **Tâches** :
   1. Créer un service API en Java Spring Boot qui renvoie une liste d’adresses.
   2. Implémenter la pagination et les filtres pour limiter les données renvoyées.
   3. Vérifier avec les logs que les optimisations réduisent bien le volume de données.

💻 **Exemple de code à adapter** :
```java
@RestController
@RequestMapping("/addresses")
public class AddressController {
    
    private static final List<String> addresses = List.of(
        "10 avenue des Champs-Élysées, Paris",
        "5 rue de la Paix, Lyon",
        "12 boulevard Haussmann, Marseille",
        "50 rue Nationale, Bordeaux"
    );
    
    @GetMapping
    public ResponseEntity<List<String>> getAddresses(@RequestParam(defaultValue = "2") int limit) {
        return ResponseEntity.ok(addresses.stream().limit(limit).toList());
    }
}
```
📌 **Résultat attendu** :
* Une API REST avec filtrage et pagination.
* **Analyse des logs** pour quantifier la réduction des données échangées.
* Documentation sur l’**optimisation dans API Green Score** et suggestion d’amélioration.

---
### Exercice 4 : Utilisation du cache pour limiter les requêtes inutiles [Facultatif]

⏱️ temps : 20mn

📌 **Objectif** : Mettre en place un système de cache pour réduire l’empreinte réseau.

📜 **Scénario** : Vous optimisez un service de musique qui surcharge le réseau avec des appels API inutiles.

✅ **Tâches** :

1. Modifier le service en Java Spring Boot pour **cacher les réponses des API externes.**
2. Comparer les temps d’exécution avec et sans cache via les logs.

💻 **Exemple avec Spring Cache :**
```java
@Service
public class MusicService {
    
    @Cacheable("categories")
    public String getCategories() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject("https://api.spotify.com/v1/browse/categories", String.class);
    }
}
```
📌 **Résultat attendu :**
* **Analyse des logs** pour quantifier l’impact du cache sur les appels API.
* Documentation sur les **bénéfices du cache dans API Green Score** et suggestion d’amélioration.
---
## 5. Conclusion & Contribution Open Source 

⏱️ temps : 15 min

✅ **Synthèse des résultats :**
* Impact des logs sur la compréhension des requêtes API.
* Gain en poids des payloads entre les différentes étapes.
* Réduction des requêtes inutiles avec le cache.

✅ **Contributions à API Green Score :**
* Explication des optimisations appliquées.
* Proposition de documentation pour **rendre le projet plus accessible aux nouveaux contributeurs.**
---
## 6. Ressources

* API Green Score
  * [API Green Score Présentation](https://www.collectif-api-thinking.com/assets/deliverables/worksites/50_CAT_API_Sustainable_IT.pdf)
  * [Grille d'évaluation](https://www.collectif-api-thinking.com/assets/deliverables/worksites/48_CAT_Sustainable_API_GreenScore_V1-2.xlsx)
  * [API Green Score Github]()
* [Postman](https://www.postman.com)
* API Gouv : 
  * https://www.data.gouv.fr/fr/dataservices : API pour accéder aux données publiques de l'État français. 
  * [API Adresse](https://geo.api.gouv.fr/adresse) : API pour obtenir des informations sur une adresse.
    
* API Spotify :
  * [API Spotify](https://developer.spotify.com/documentation/web-api/reference/) : API pour récupérer des métadonnées ou contrôler la lecture de contenu Spotify.
---    
## 8. Définitions

🔑 **1. Payload**

* **Définition** : Ensemble des données transmises dans une requête ou une réponse HTTP.
* **Exemple** : Dans une requête POST pour créer un utilisateur, le payload peut être :

  `{"nom": "Dupont", "email": "dupont@mail.com"}.`
  
  Plus le payload est gros, plus il consomme de bande passante et d’énergie.

🔑 **2. Cache**
  
* **Définition**  : Mécanisme de stockage temporaire des données pour éviter des traitements ou appels redondants.
* **Exemple** : Une API de météo stocke les résultats pendant 10 minutes pour éviter de redemander à chaque utilisateur la même info → gain de temps et d’énergie.

🔑 **3. Log**

* **Définition** : Message enregistré pour tracer l’activité d’un système (événement, erreur, performance…).
* **Exemple** : Un log d’API peut enregistrer :

    `"GET /api/books | 200 OK | 125ms | 1.2kB"`

    → Utilisé pour analyser la charge ou optimiser une API.

🔑 **4. API (Application Programming Interface)**

* **Définition** : Interface permettant à deux systèmes de communiquer via des appels standardisés.
* **Exemple** : Une application mobile utilise une API pour récupérer les livres disponibles sur un serveur distant.

🔑 **5. JSON vs XML**

* **Définition** : Deux formats de données utilisés pour échanger des informations entre systèmes.

* **Exemple** : JSON est souvent plus léger que XML.

  * JSON : `{"titre": "Harry Potter"}`
  * XML : `<livre><titre>Harry Potter</titre></livre>`
  
  → JSON a un impact environnemental plus faible.

🔑 **6. Pagination**

* **Définition** : Technique qui divise les résultats d'une API en plusieurs pages.

* **Exemple** : Plutôt que de retourner 1 000 livres d’un coup, on retourne 20 résultats par page → réduit le payload et donc la consommation.

🔑 **7. Filtrage**

* **Définition** : Limitation des résultats d’une API en fonction de critères.

* **Exemple** : `/api/books?lang=fr&year=2020` → ne retourne que les livres en français publiés en 2020 → données plus ciblées, moins lourdes.

🔑 **8. Compression**

* **Définition** : Réduction de la taille des données échangées via des algorithmes comme GZIP.

* **Exemple** : Une réponse API de 500 ko compressée peut descendre à 50 ko → gain de bande passante et d’énergie.

🔑 **9. Bande passante**

* **Définition** : Quantité de données pouvant transiter sur un réseau pendant une période donnée.

* **Exemple** : Transmettre 10 Mo de données inutilement surcharge le réseau et consomme plus d’énergie, d'où l’intérêt de payloads optimisés.

🔑 **10. Empreinte environnementale du numérique**

* **Définition** : Impact écologique du cycle de vie des services numériques (fabrication, utilisation, transfert des données, etc.).

* **Exemple** : Une API mal conçue qui génère des appels inutiles, des données volumineuses et non compressées augmente l’empreinte carbone du service.
