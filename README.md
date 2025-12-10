# EmailSender

_A service in the messaging ecosystem whic purpose is to send emails. Has the capability to send emails via SMTP and or Microsoft graph._

## Getting Started

### Prerequisites

- **Java 25 or higher**
- **Maven**
- **Git**
- **[Dependent Microservices](#dependencies)**

### Installation

1. **Clone the repository:**

   ```bash
   git clone https://github.com/Sundsvallskommun/YOUR-PROJECT-ID.git
   cd YOUR-PROJECT-ID
   ```
2. **Configure the application:**

   Before running the application, you need to set up configuration settings.
   See [Configuration](#Configuration)

   **Note:** Ensure all required configurations are set; otherwise, the application may fail to start.

3. **Ensure dependent services are running:**

   If this microservice depends on other services, make sure they are up and accessible. See [Dependencies](#dependencies) for more details.

4. **Build and run the application:**

   ```bash
   mvn spring-boot:run
   ```

## Dependencies

This microservice depends on the following services:

- **Microsoft Graph (Optional)**
  - **Purpose:** To send emails from a Microsoft 365 cloud based organisation.
  - **Repository:** [Link to the repository](https://learn.microsoft.com/sv-se/graph/overview)
  - **Setup Instructions:** Refer to its documentation for installation and configuration steps.

Ensure that these services are running and properly configured before starting this microservice.

## API Documentation

Access the API documentation via Swagger UI:

- **Swagger UI:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

Alternatively, refer to the `openapi.yml` file located in the project's root directory for the OpenAPI specification.

## Usage

### API Endpoints

Refer to the [API Documentation](#api-documentation) for detailed information on available endpoints.

### Example Request

```bash
curl -X POST http://localhost:8080/api/2281/send/email \ 
  -H "Content-Type: application/json" \
  -d '{
        "headers": {
            "Message-ID": [
                "<0d151193@113c2234d334>"
            ]
        },
        "emailAddress": "recipient@somehost.com",
        "attachments": [
            {
                "name": "myattachment.txt",
                "contentType": "text/plain",
                "content": "dGhpcyBpcyBhbiBhdHRhY2htZW50"
            }
        ],
        "sender": {
            "address": "customerservice@somedummyhost.com",
            "name": "Customer Service",
            "replyTo": "noreply@somedummyhost.com"
        },
        "subject": "Order confirmation",
        "htmlMessage": "TG9yZW0gaXBzdW0gZG9sb3Igc2l0IGFtZXQ=",
        "message": "Lorem ipsum dolor sit amet"
    }'
```

## Configuration

Configuration is crucial for the application to run successfully. Ensure all necessary settings are configured in `application.yml`.

### Key Configuration Parameters

- **Server Port:**

  ```yaml
  server:
    port: 8080
  ```
- **External Service URLs:**

  ```yaml
    email:
      instances:
        2281:
          basic: #smtp
            host: smtp.somehost.com
            port: 25
        2282:
          azure: #graph
            scope: https://graph.microsoft.com/.default

  ```
- **No additional setup is required** for database initialization, as long as the database connection settings are correctly configured.

### Additional Notes

- **Application Profiles:**

  Use Spring profiles (`dev`, `prod`, etc.) to manage different configurations for different environments.

- **Logging Configuration:**

  Adjust logging levels if necessary.

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](https://github.com/Sundsvallskommun/.github/blob/main/.github/CONTRIBUTING.md) for guidelines.

## License

This project is licensed under the [MIT License](LICENSE).

## Code status

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-email-sender&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-email-sender)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-email-sender&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-email-sender)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-email-sender&metric=security_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-email-sender)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-email-sender&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-email-sender)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-email-sender&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-email-sender)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-email-sender&metric=bugs)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-email-sender)

## 

Copyright (c) 2021 Sundsvalls kommun
