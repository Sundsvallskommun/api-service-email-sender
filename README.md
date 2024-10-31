# EmailSender

## Leverantör

Sundsvalls Kommun

## Beskrivning

EmailSender används för att skicka e-post.

## Tekniska detaljer

### Konfiguration

|        Miljövariabel         |              Beskrivning              |
|------------------------------|---------------------------------------|
| `integration.email.hostname` | SMTP-serverns hostname/IP-adress      |
| `integration.email.port`     | SMTP-serverns port (standardvärde 25) |
| `integration.email.username` | Användarnamn till SMTP-servern        |
| `integration.email.password` | Lösenord till SMTP-servern            |

### Paketera och starta tjänsten

Tjänsten kan paketeras genom:

```
mvn package
```

Starta med:

```
java -jar target/api-service-email-sender-<VERSION>.jar
```

### Bygga och starta tjänsten med Docker

Bygg en Docker-image av tjänsten:

```
mvn spring-boot:build-image
```

Starta en Docker-container:

```
docker run -i --rm -p 8080:8080 evil.sundsvall.se/ms-email-sender:latest
```

## Status

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-email-sender&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-email-sender)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-email-sender&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-email-sender)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-email-sender&metric=security_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-email-sender)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-email-sender&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-email-sender)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-email-sender&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-email-sender)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-email-sender&metric=bugs)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-email-sender)

## 

Copyright (c) 2021 Sundsvalls kommun
