# EmailSender

## Leverantör
Sundsvalls Kommun

## Beskrivning
EmailSender används för att skicka e-post.

## Tekniska detaljer

### Konfiguration

Konfiguration sker i filen `src/main/resources/application.properties` genom att sätta nedanstående properties till önskade värden:

|Property|Beskrivning|
|---|---|
|`integration.email.hostname`|SMTP-serverns hostname/IP-adress|
|`integration.email.port`|SMTP-serverns port|
|`integration.email.username`|Användarnamn till SMTP-servern|
|`integration.email.password`|Lösenord till SMTP-servern|


### Paketera och starta tjänsten

Paketera tjänsten som en körbar JAR-fil genom:

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
