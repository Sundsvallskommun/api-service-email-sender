# EmailSender

## Leverantör
Sundsvalls Kommun

## Beskrivning
EmailSender används för att skicka e-post.

## Tekniska detaljer

### Konfiguration

|Miljövariabel|Beskrivning|
|---|---|
|`integration.email.hostname`|SMTP-serverns hostname/IP-adress|
|`integration.email.port`|SMTP-serverns port (standardvärde 25)|
|`integration.email.username`|Användarnamn till SMTP-servern|
|`integration.email.password`|Lösenord till SMTP-servern|

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

Copyright &copy; 2022 Sundsvalls Kommun
