# GitHub Repository Searcher (RepoFinder)

A Spring Boot REST API that searches GitHub repositories, stores repository details in PostgreSQL, and provides filtering and sorting for saved repositories.

## Live Deployment

- **Base URL:** https://repofinder.onrender.com
- **Swagger UI:** https://repofinder.onrender.com/swagger-ui/index.html
- **OpenAPI Documentation:** https://repofinder.onrender.com/v3/api-docs

The application is deployed on Render using Docker and connected to a hosted PostgreSQL database. Both search and retrieval endpoints have been tested successfully in the deployed environment.

## Features

- Search GitHub repositories using a search query.
- Filter search results by programming language.
- Store repository information in PostgreSQL.
- Update existing repository records instead of creating duplicates.
- Retrieve saved repositories from the database.
- Filter repositories by language and minimum stars.
- Sort repositories by stars, forks, and last updated date.
- Handle empty results and invalid requests.
- Provide consistent error responses.
- Interactive API documentation using Swagger UI.

## Technology Stack

| Category | Technologies |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.1.2 |
| Database | PostgreSQL |
| ORM | Spring Data JPA, Hibernate |
| External API | GitHub REST API |
| API Documentation | Springdoc OpenAPI / Swagger |
| Testing | JUnit 5, Mockito, MockMvc |
| Build Tool | Maven |
| Deployment | Docker, Render |

## Project Structure

```text
RepoFinder/
├── src/
│   ├── main/
│   │   ├── java/com/RepoFinder/RepoFinder/
│   │   │   ├── Client/
│   │   │   ├── Controller/
│   │   │   ├── Entity/
│   │   │   ├── Repository/
│   │   │   ├── Service/
│   │   │   └── RepoFinderApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
├── Dockerfile
├── .dockerignore
├── pom.xml
└── README.md
```

## API Endpoints

### 1. Search GitHub Repositories

**POST** `/api/github/search`

Searches GitHub using the supplied query and saves the returned repository details in PostgreSQL.

Example:

```http
POST https://repofinder.onrender.com/api/github/search?query=spring%20boot&language=Java&sort=stars
```

Parameters:

| Parameter | Required | Description |
|---|---|---|
| query | Yes | GitHub repository search query |
| language | No | Programming language filter |
| sort | No | Sorting option; defaults to stars |

### 2. Retrieve Stored Repositories

**GET** `/api/github/repositories`

Retrieves saved repositories from PostgreSQL with optional filtering and sorting.

Example:

```http
GET https://repofinder.onrender.com/api/github/repositories?language=Java&minStars=100&sort=stars
```

Parameters:

| Parameter | Required | Description |
|---|---|---|
| language | No | Filter by programming language |
| minStars | No | Minimum number of stars |
| minForks | No | Minimum number of forks |
| sort | No | Sorting option; defaults to stars |

Supported sorting fields include stars, forks, and last updated date.

## Local Setup

### Prerequisites

- Java 25
- Maven
- PostgreSQL
- Git

### 1. Clone the Repository

```bash
git clone https://github.com/bharathraj49/RepoFinder
cd RepoFinder
```

Replace the placeholders with your actual GitHub repository details.

### 2. Configure PostgreSQL

Create a PostgreSQL database:

```sql
CREATE DATABASE repofinder;
```

Configure your database connection in `src/main/resources/application.properties`:

```properties
spring.application.name=RepoFinder

server.port=${PORT:8088}

spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/repofinder}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:YOUR_DB_USERNAME:bharatht}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:YOUR_DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Use your own local PostgreSQL credentials. Do not commit database passwords or production secrets to GitHub.

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

The application runs locally at:

```text
http://localhost:8088
```

### 4. Access Swagger UI

```text
http://localhost:8088/swagger-ui/index.html
```

## Running Tests

Run the test suite using:

```bash
./mvnw test
```

The project includes:

- Service unit tests
- Controller tests using MockMvc
- Integration tests with a real PostgreSQL database
- Tests for successful searches and empty results
- Tests for filtering and sorting
- Tests for invalid requests and error handling
- Tests for updating existing repository records

## Docker Deployment

The application uses a multi-stage Docker build.

Build the Docker image:

```bash
docker build -t repofinder .
```

Run the container:

```bash
docker run -p 8088:8088 repofinder
```

Configure the database environment variables when running the application in a deployed environment.

## Render Deployment

The application is deployed on Render with:

- Docker as the runtime.
- A PostgreSQL database hosted on Render.
- Environment variables for database configuration.
- Render's assigned port through `PORT`.

Configure these environment variables in the Render web service:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
```

The deployed application is available at:

https://repofinder.onrender.com

## Error Handling

The application uses a global exception handler to return consistent error responses for cases such as:

- Missing required request parameters
- Invalid parameter types
- Invalid sorting options
- Unexpected application errors

## Future Improvements

- Add pagination for repository retrieval.
- Introduce caching to reduce repeated GitHub API requests.
- Improve API rate-limit handling.
- Add authentication and authorization if required.

## Author

**Bharath Raj**

GitHub: https://github.com/bharathraj49