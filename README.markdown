# MediConnect FHIR System 🏥

## Description
MediConnect is a modern medical management system designed to streamline patient and diagnostic management for healthcare professionals using FHIR (Fast Healthcare Interoperability Resources) standards. This project integrates a backend with two services (`patient-api` and `condition-api`) and a responsive Angular 19 frontend, providing a scalable and standards-compliant platform.

## Key Features
- **Patient Management**:
  - Create, update, search, and retrieve patient information.
  - Soft delete functionality for patients.
  - FHIR-compliant patient data handling.
- **Diagnostic Management**:
  - Create and link diagnostics to patients using FHIR Condition resources.
  - Retrieve patient medical history as a timeline.
- **Frontend Interface**:
  - Modern, responsive UI with Angular 19 and Angular Material Design.
  - Includes patient management and diagnostic tracking screens.
- **API Integration**:
  - RESTful APIs with HAPI FHIR Server integration.
  - WebClient for communication between `patient-api` and `condition-api`.
- **Additional Features**:
  - Data validation for FHIR resources.
  - Swagger/OpenAPI documentation.
  - Containerization with Docker and Docker Compose.

## Technologies Used
- **Backend**: Java, Spring Boot, HAPI FHIR Server (R4), Spring WebFlux
- **Database**: PostgreSQL
- **Frontend**: Angular 19, Angular Material Design
- **DevOps**: Docker, Docker Compose, Git
- **Tools**: Maven, Flyway (for `condition-api`), Swagger

## Project Structure
```
mediconnect-challenge/
├── README.md                    # Project overview and instructions
├── docker-compose.yml           # Global orchestration for all services
├── .gitignore                   # Git ignore file
├── docs/                        # Documentation folder
│   ├── architecture.md          # Technical architecture details
│   ├── api-documentation.md     # API documentation
│   └── deployment.md            # Deployment instructions
├── patient-api/                 # Patient management service
│   ├── src/                     # Source code
│   │   ├── main/
│   │   │   ├── java/com/mediconnect/patient/
│   │   │   │   ├── PatientApiApplication.java
│   │   │   │   ├── config/      # Configuration classes
│   │   │   │   ├── controller/  # REST controllers
│   │   │   │   ├── service/     # Business logic
│   │   │   │   ├── repository/  # Data access
│   │   │   │   ├── model/       # Entities
│   │   │   │   ├── dto/         # Data transfer objects
│   │   │   │   ├── mapper/      # Object mappers
│   │   │   │   ├── exception/   # Custom exceptions
│   │   │   │   └── validation/  # FHIR validation
│   │   │   └── resources/       # Configuration and migrations
│   │   │       ├── application.yml
│   │   │       └── db/migration/
│   │   └── test/                # Unit tests
│   ├── pom.xml                  # Maven configuration
│   ├── Dockerfile               # Docker configuration
│   ├── docker-compose.yml       # Service-specific Docker Compose
│   └── README.md                # Service-specific documentation
├── condition-api/               # Condition management service
│   ├── src/                     # Source code
│   │   ├── main/
│   │   │   ├── java/com/mediconnect/condition/
│   │   │   │   ├── ConditionApiApplication.java
│   │   │   │   ├── config/      # Configuration classes
│   │   │   │   ├── controller/  # REST controllers
│   │   │   │   ├── service/     # Business logic
│   │   │   │   ├── repository/  # Data access
│   │   │   │   ├── model/       # Entities
│   │   │   │   ├── dto/         # Data transfer objects
│   │   │   │   ├── mapper/      # Object mappers
│   │   │   │   ├── exception/   # Custom exceptions
│   │   │   │   ├── validation/  # FHIR validation
│   │   │   │   └── client/      # WebClient for patient-api
│   │   │   └── resources/       # Configuration and migrations
│   │   └── test/                # Unit tests
│   ├── pom.xml                  # Maven configuration
│   ├── Dockerfile               # Docker configuration
│   ├── docker-compose.yml       # Service-specific Docker Compose
│   └── README.md                # Service-specific documentation
└── frontend/                    # Angular frontend
    ├── src/                     # Source code
    ├── package.json             # Node.js dependencies
    ├── angular.json             # Angular configuration
    ├── Dockerfile               # Docker configuration
    └── README.md                # Frontend documentation
```

## Installation 🚀

### Prerequisites
- **Java**: JDK 17 or higher
- **Node.js**: Version 18 or higher
- **Docker**: For containerization
- **PostgreSQL**: For local database setup
- **Maven**: For building backend services
- **Git**: For version control

### Setup Instructions

#### 1. Clone the Repository
```bash
git clone https://github.com/[Your-Username]/mediconnect-challenge.git
cd mediconnect-challenge
```

#### 2. Set Up the Databases
- Ensure PostgreSQL is running locally.
- Create two databases:
  - `patient_db` for `patient-api`.
  - `condition_db` for `condition-api`.
- Run the following commands in your PostgreSQL client:
  ```sql
  CREATE DATABASE patient_db;
  CREATE DATABASE condition_db;
  ```
- The default credentials are pre-configured in `application.yml` files:
  - `patient_db`: `patient_user` / `patient_pass` (host: `localhost:5433`)
  - `condition_db`: `condition_user` / `condition_pass` (host: `localhost:5434`)

#### 3. Run with Docker Compose (Recommended)
- A `docker-compose.yml` file is provided at the root to orchestrate all services.
- From the root directory, execute:
  ```bash
  docker-compose up --build
  ```
- Access the services:
  - **Patient API**: `http://localhost:8080/fhir`
  - **Condition API**: `http://localhost:8082/fhir`
  - **Frontend**: `http://localhost:4200`

#### 4. Run Locally (Manual Setup)
- **Patient API**:
  ```bash
  cd patient-api
  mvn clean install
  mvn spring-boot:run
  ```
- **Condition API**:
  ```bash
  cd condition-api
  mvn clean install
  mvn spring-boot:run
  ```
- **Frontend**:
  ```bash
  cd frontend
  npm install
  ng serve
  ```

## API Endpoints

### Patient API (`patient-api`)
| Method | Endpoint                          | Description                          |
|--------|-----------------------------------|--------------------------------------|
| GET    | `/fhir/health`                   | Health check for the API             |
| POST   | `/fhir/Patient`                  | Create a new patient (DTO)           |
| GET    | `/fhir/Patient?name={name}`      | Search patients by name              |
| GET    | `/fhir/Patient?identifier={id}`  | Search patients by identifier        |
| GET    | `/fhir/Patient/{id}`             | Get patient details by FHIR ID       |
| PUT    | `/fhir/Patient/{id}`             | Update patient details               |
| DELETE | `/fhir/Patient/{id}`             | Soft delete a patient                |
| POST   | `/fhir/Patient/fhir`             | Create patient from FHIR resource    |
| GET    | `/fhir/Patient/{id}/fhir`        | Get patient as FHIR resource         |
| GET    | `/fhir/Patient/{id}/exists`      | Check if patient exists by ID        |
| GET    | `/fhir/Patient/count`            | Get active patient count             |

### Condition API (`condition-api`)
| Method | Endpoint                          | Description                          |
|--------|-----------------------------------|--------------------------------------|
| POST   | `/fhir/Condition`                | Create a new condition               |
| GET    | `/fhir/Condition?patient={id}`   | Get conditions for a patient         |
| GET    | `/fhir/Condition/{id}`           | Get condition details by ID          |
| PUT    | `/fhir/Condition/{id}`           | Update condition details             |

- Detailed API documentation is available via Swagger:
  - `patient-api`: `http://localhost:8080/swagger-ui.html`
  - `condition-api`: `http://localhost:8082/swagger-ui.html`

## Database Setup
- **Patient Database (`patient_db`)**:
  - Host: `localhost:5433`
  - Username: `patient_user`
  - Password: `patient_pass`
- **Condition Database (`condition_db`)**:
  - Host: `localhost:5434`
  - Username: `condition_user`
  - Password: `condition_pass`
  - Uses Flyway for migrations (scripts in `condition-api/src/main/resources/db/migration`).
  - `conditions` table aligns with FHIR Condition resources (e.g., `code_system`, `code_value`, `patient_reference`).

## Development

### Running Tests
- **Patient API**:
  ```bash
  cd patient-api
  mvn test
  ```
- **Condition API**:
  ```bash
  cd condition-api
  mvn test
  ```
- **Frontend**:
  ```bash
  cd frontend
  ng test
  ```

### Database Migrations
- Flyway is enabled for `condition-api`. Migration scripts are located in `condition-api/src/main/resources/db/migration`.

## Contribution
1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/YourFeature`).
3. Commit your changes (`git commit -m 'Add YourFeature'`).
4. Push to the branch (`git push origin feature/YourFeature`).
5. Open a Pull Request.

## Submission
- **GitHub Repository**: [Your GitHub Link]
- **Email**: Submit to `digitalhealthafrigroup@gmail.com` with subject: `Challenge MediConnect - [Your Name] - Candidature Stagiaire`

## License
This project is licensed under the MIT License. See the `LICENSE` file for details.

## Final Note
MediConnect is a journey into modern healthcare technology! Explore, contribute, and make it your own. 🚀

*"Code is like medicine: it requires precision, empathy, and a touch of creativity!"*