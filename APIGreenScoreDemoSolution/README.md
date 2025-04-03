#API Green Score Demo

This is a detailed guide to creating a Spring Boot project that exposes a REST API. This example will include the following:
1. **Project Setup**: Using Spring Initializr to generate the project structure.
2. **Dependencies**: Adding necessary dependencies.
3. **Application Configuration**: Configuring the application properties.
4. **Creating a REST Controller**: Implementing a simple REST controller.
5. **Running the Application**: Running the Spring Boot application.
6. **Testing the API**: Testing the API using cURL or Postman.

### Step 1: Project Setup

1. **Open Spring Initializr**:
    - Go to [Spring Initializr](https://start.spring.io/).
    - Choose the following options:
        - **Project**: Maven Project
        - **Language**: Java
        - **Spring Boot**: Latest stable version
        - **Project Metadata**:
            - **Group**: com.example
            - **Artifact**: springboot-restapi
            - **Name**: springboot-restapi
            - **Description**: A simple Spring Boot REST API
            - **Package name**: com.example.springbootrestapi
            - **Packaging**: Jar
            - **Java**: 11 or higher
        - **Dependencies**:
            - Spring Web
            - Spring Data JPA (if you plan to use a database)
            - H2 Database (for in-memory database, optional)

2. **Generate and Download the Project**:
    - Click on "Generate" to download the project as a ZIP file.
    - Extract the ZIP file and import it into your IDE (e.g., IntelliJ IDEA or Eclipse).

### Step 2: Dependencies

Ensure your `pom.xml` includes the necessary dependencies. For this example, we only need `spring-boot-starter-web`.

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

### Step 3: Application Configuration

Create or modify the `application.properties` file in the `src/main/resources` directory to configure the application. For this example, we don't need any specific configurations, but you can add them if needed.

```properties
# Example configuration (optional)
server.port=8080
```

### Step 4: Creating a REST Controller

Create a new package named `controller` inside `src/main/java/com/example/springbootrestapi`. Inside this package, create a class named `HelloController.java`.

```java
package com.example.springbootrestapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World!";
    }
}
```

### Step 5: Running the Application

1. **Run the Application**:
    - In your IDE, run the `SpringbootRestapiApplication.java` class located in `src/main/java/com/example/springbootrestapi`.
    - Alternatively, you can run the application from the command line:
      ```sh
      ./mvnw spring-boot:run
      ```

2. **Access the API**:
    - Open a web browser or use a tool like cURL or Postman to access the API.
    - Navigate to `http://localhost:8080/api/hello`.

### Step 6: Testing the API

You can test the API using cURL:

```sh
curl http://localhost:8080/api/hello
```

Expected output:
```
Hello, World!
```

### Additional Notes

- **Error Handling**: You can add custom error handling by creating an `@ControllerAdvice` class.
- **Logging**: Configure logging in `application.properties` to log more detailed information.
- **Security**: Add Spring Security for securing your API if needed.

This setup provides a basic Spring Boot application with a REST API. You can expand it by adding more controllers, services, and repositories as required.