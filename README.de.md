[Java]: https://img.shields.io/badge/Java%2011-rgb(235%2C%20149%2C%2042)?style=for-the-badge
[API]: https://img.shields.io/badge/API-Wrapper-blue?style=for-the-badge
[License]: https://img.shields.io/badge/License-Apache%202.0-white?style=for-the-badge
[Version]: https://img.shields.io/maven-central/v/io.github.shurablack/JIMA?strategy=highestVersion&style=for-the-badge&color=green
[Discord]: https://img.shields.io/badge/Discord-shurablack-rgb(2%2C%20187%2C%20249)?style=for-the-badge&logo=discord&logoColor=rgb(2%2C%20187%2C%20249)

# 📦 JIMA (Java Idle MMO API)

[![Java][]][Java]
[![API][]][API]
[![License][]][License]
[![Version][]][Version]
[![Discord][]][Discord]

**JIMA** ist ein Java-basierter API-Wrapper für die Idle MMO API. Die Bibliothek bietet eine intuitive, objektorientierte Schnittstelle für den Zugriff auf Spieldaten wie Charaktere, Gilden, Gegenstände, Dungeons und vieles mehr.

---

## 📋 Inhaltsverzeichnis

<details open>
<summary><b>Zum Expandieren/Zuklappen klicken</b></summary>

- [Features](#-features)
- [Schnellstart](#-schnellstart)
- [Installation](#-installation)
- [Verwendung](#-verwendung)
  - [Requester](#1-requester--haupteinstiegspunkt)
  - [RequestManager](#2-requestmanager--anfrageverwaltung)
  - [TokenStore](#3-tokenstore--token-verwaltung)
- [API-Referenz](#-unterstützte-api-endpoints)
- [Projektstruktur](#-projektstruktur)
- [Best Practices](#-best-practices)
- [Abhängigkeiten](#-abhängigkeiten)
- [Lizenz](#-lizenz)

</details>

---

## ⚡ Schnellstart

<details>
<summary><b>Erste Schritte in 5 Minuten</b></summary>

**1. Abhängigkeit hinzufügen:**
```gradle
dependencies {
    implementation 'io.github.shurablack:JIMA:$version'
}
```

**2. Erstelle `jima-config.properties`:**
```properties
API_KEY=dein_token_hier
CONTACT_EMAIL=deine_email@example.com
APPLICATION_VERSION=1.0.0
APPLICATION_NAME=MeineApp
```

**3. Beginne mit JIMA:**
```java
// Weltbosse abrufen
var response = Requester.getWorldBosses();
if (response.isSuccessful()) {
    var bosses = response.getData().getWorldBosses();
    bosses.forEach(boss -> System.out.println(boss.getName()));
}

// Charakterinformationen abrufen
var charResponse = Requester.getCharacter("characterId");
if (charResponse.isSuccessful()) {
    var character = charResponse.getData().getCharacter();
    System.out.println("Level: " + character.getLevel());
}
```

**4. Für fortgeschrittene Verwendung siehe [Verwendungsabschnitt](#-verwendung)**

</details>

## ✨ Features

- 🧱 Objektorientierte Darstellung von Spielentitäten
- 💡 Verwaltete Requests ohne zusätzlichen Code
- ⚡ Benutzerfreundliche Request-Methoden für nahtlosen Datenabruf
- 💾 Eingebaute Konfigurationsverwaltung für API-Schlüssel und Anwendungseinstellungen
- 🔄 Unterstützung für rotierende Tokens zur Verbesserung der Rate Limits
- 🌐 Umfassende Unterstützung für Spielfeatures wie Weltbosse, Gildeneroberungen und Markthistorie
- 🔒 Sichere Token-Verwaltung mit automatischer Rate-Limit-Verfolgung

## 📑 Hinweis

> [!WARNING]
> **Dieses Projekt ist nicht mit IdleMMO verbunden.** Dies ist eine unabhängige Initiative der Community.

Dieses Projekt ist eine unabhängige Initiative und wird weder von den Schöpfern von IdleMMO anerkannt noch unterstützt. Bitte respektieren Sie die Nutzungsbedingungen des Spiels und vermeiden Sie übermäßige API-Anfragen, die die Serverperformance beeinträchtigen könnten.

> **Access Token** und **Scopes** werden vom Benutzer dieser Bibliothek verwaltet. Stellen Sie sicher, dass Sie die Authentifizierung sicher und verantwortungsvoll verarbeiten.

## 🔑 Beschaffung eines Access Tokens

Um einen Access Token für die Idle MMO API zu erhalten, gehen Sie wie folgt vor:

- Besuchen Sie [Settings/PublicAPI](https://web.idle-mmo.com/settings/api)
- Klicken Sie auf die Schaltfläche "Create New Key"
- Geben Sie den Schlüsselnamen und die Gültigkeitsdauer (Tage) ein und legen Sie die API-Scopes nach Bedarf fest
- Klicken Sie auf "Generate Key", um den Token zu erstellen
- Kopieren Sie den erzeugten Token und verwenden Sie ihn in Ihrer Anwendungskonfiguration

## 🔧 Installation

> ✅ **Anforderungen:** Java 11+ | Verfügbar auf Maven Central

### Gradle

```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.shurablack:JIMA:$version'
}
```

### Maven

```xml
<dependency>
    <groupId>io.github.shurablack</groupId>
    <artifactId>JIMA</artifactId>
    <version>$version</version>
</dependency>
```

### Konfiguration

Erstellen Sie eine `jima-config.properties` Datei:

```properties
API_KEY=<your_token>
CONTACT_EMAIL=<your_email>
APPLICATION_VERSION=<app_version>
APPLICATION_NAME=<app_name>
USE_ROTATING_TOKENS=<true/false>
```

> [!NOTE]
> Sie können auch Umgebungsvariablen statt einer Properties-Datei verwenden.

> [!TIP]
> Die Anwendung erzeugt automatisch eine Konfigurationsvorlage, falls sie nicht existiert.

> [!IMPORTANT]
> Für rotierende Tokens erstellen Sie `jima-tokens.txt` mit je einem Token pro Zeile.

> [!CAUTION]
> Die Verwendung von Tokens anderer Spieler erfordert eine Offenlegung gemäß [API-Richtlinien](https://web.idle-mmo.com/wiki/more/api). Es ist nicht erlaubt, mehrere Konten zu erstellen, um die Rate Limits zu verlängern.

## 🧪 Verwendung

Die Bibliothek bietet drei Hauptkomponenten für die API-Interaktion:

### 1. Requester – Haupteinstiegspunkt

Die `Requester` Klasse ist die primäre Schnittstelle für API-Anfragen. Sie stellt statische Methoden für alle unterstützten Endpoints bereit.

**Beispiel: Weltbosse abrufen**

```java
var response = Requester.getWorldBosses();
if (response.isSuccessful()) {
    WorldBosses bosses = response.getData();
    bosses.getWorldBosses().forEach(boss -> {
        System.out.println("Boss Name: " + boss.getName());
        System.out.println("Ort: " + boss.getLocation().getName());
    });
} else {
    System.err.println("Fehler: " + response.getError());
}
```

**Beispiel: Charakterinformationen abrufen**

```java
var response = Requester.getCharacter("hashedCharacterId");
if (response.isSuccessful()) {
    CharacterView view = response.getData();
    System.out.println("Charaktername: " + view.getCharacter().getName());
    System.out.println("Klasse: " + view.getCharacter().getClassType());
} else {
    System.err.println("Fehler: " + response.getError());
}
```

**Beispiel: Erweiterte Itemsuche**

```java
// Fuzzy Matching mit Tippfehlertoleranz
var response = Requester.advancedSearchItems("Holz Schwert");
if (response.isSuccessful()) {
    Items items = response.getData();
    items.getItems().forEach(item -> {
        System.out.println("Item: " + item.getName() + " (ID: " + item.getHashedId() + ")");
    });
}

// Alle Items eines Typs abrufen
var response = Requester.searchType(ItemType.WEAPON);
```

### 2. RequestManager – Anfrageverwaltung

Der `RequestManager` ist ein Singleton, das die gesamte HTTP-Kommunikation mit Rate-Limiting und automatischem Retry verwaltet. Sie müssen ihn normalerweise nicht direkt verwenden, da `Requester` ihn automatisch nutzt.

**Automatische Funktionen:**

- **Rate-Limiting**: Unterschreitet die verbleibenden Anfragen ein konfiguriertes Minimum, wird der Retry automatisch zeitgesteuert
- **Asynchrone Verarbeitung**: Alle Requests sind asynchron mit `CompletableFuture`
- **Fehlerbehandlung**: Automatisches Logging und Fehlerbehandlung mit Log4j2
- **Konfigurierbar**: Log-Level und Nutzungsgrenzen können zur Laufzeit angepasst werden

**Optionale Konfiguration:**

```java
// Log-Level einstellen
RequestManager.setLogLevel(Level.DEBUG);

// Nutzungslimit einstellen (Retry bei weniger als X verbleibenden Requests)
RequestManager.getInstance().setUsageLimit(5);
```

### 3. TokenStore – Token-Verwaltung

Der `TokenStore` ist ein Thread-sicherer Singleton für die Verwaltung mehrerer API-Tokens. Dies ist besonders nützlich für höhere Rate Limits.

**Features:**

- **Token-Rotation**: Tokens werden nach verbleibenden Anfragen sortiert (absteigend)
- **Rate-Limit-Tracking**: Jeder Token wird mit verbleibenden Anfragen und Reset-Zeit verwaltet
- **In flight Umleitung:** Wenn eine Anfrage für denselben Endpoint und dieselben Parameter bereits in Bearbeitung ist, wird das bestehende `CompletableFuture` zurückgegeben, anstatt eine neue Anfrage zu stellen
- **Thread-Sicher**: Nutzt `ConcurrentSkipListSet` für sichere Multithreading-Operationen
- **Automatisches Laden**: Hat eine `loadTokens()` Methode zum Laden aus `jima-tokens.txt`

> [!TIP]
> Optional kann das cachen von Endpunkten mit einem endpoint_update_at mit `RequestManager.enableEndpointCaching(<recordStats:boolean>)` aktiviert werden

**Beispiel: Rotierende Tokens verwenden**

```java
// Tokens laden (aus jima-tokens.txt)
TokenStore.getInstance().loadTokens();

// Token hinzufügen
TokenStore.getInstance().addToken("your_api_token");

// Authentifizierungen aller gespeicherten Tokens abrufen
List<Authentication> auths = TokenStore.getInstance().getTokenAuthentications();
auths.forEach(auth -> {
    System.out.println("Benutzer: " + auth.getAccount().getUsername());
    System.out.println("Verbleibende Anfragen: " + auth.getRateLimitRemaining());
});
```

> [!TIP]
> - Der `ApiObjectMapper` definiert Jackson-Module und einen ObjectMapper für Serialisierung/Deserialisierung.<br>
> - Der `ImageLoader` ist eine Convenience-Klasse zum Laden von Bildern aus URLs.<br>
> - API-Aufrufe sind standardmäßig blockierend (`join()` auf `CompletableFuture`), können aber asynchron verwendet werden.

### 4. Batch-Operationen – Parallele Anfragen

Für verbesserte Performance bei mehreren Anfragen verwenden Sie die Batch-Operationsmethoden, die Anfragen parallel ausführen:

**Beispiel: Mehrere Charaktere abrufen**

```java
List<String> characterIds = Arrays.asList("id1", "id2", "id3");
List<Response<CharacterView>> responses = Requester.getMultipleCharacters(characterIds);

// Nur erfolgreiche Responses filtern
List<CharacterView> characters = responses.stream()
    .filter(Response::isSuccessful)
    .map(Response::getData)
    .collect(Collectors.toList());
```

**Verfügbare Batch-Methoden:**

```java
// Mehrere Charaktere parallel abrufen
Requester.getMultipleCharacters(List<String> characterIds);

// Mehrere Gilden parallel abrufen
Requester.getMultipleGuilds(List<Integer> guildIds);

// Mehrere Gildenmitgliederlisten parallel abrufen
Requester.getMultipleGuildMembers(List<Integer> guildIds);

// Mehrere Item-Inspektor-Ansichten parallel abrufen
Requester.getMultipleItemInspections(List<String> itemIds);
```

### 5. PaginationHelper – Automatisches Abrufen von Mehrseiten-Ergebnissen

Für Endpoints, die paginierte Ergebnisse zurückgeben, verarbeitet das `PaginationHelper`-Dienstprogramm automatisch das Abrufen aller Seiten und das Kombinieren von Ergebnissen:

**Beispiel: Alle Items eines Typs abrufen**

```java
// Ruft automatisch alle Seiten ab und kombiniert Ergebnisse
List<Item> swords = PaginationHelper.fetchAllItemsByType(ItemType.SWORD);
System.out.println("Gesamtzahl der Schwerter: " + swords.size());

// Mehrere Item-Typen abrufen
List<ItemType> armorTypes = Arrays.asList(
    ItemType.CHESTPLATE, ItemType.HELMET, ItemType.BOOTS, ItemType.GREAVES
);
List<Item> armorItems = PaginationHelper.fetchAllItemsByTypes(armorTypes);
```

**Verfügbare PaginationHelper-Methoden:**

```java
// Alle Items eines bestimmten Typs über alle Seiten abrufen
PaginationHelper.fetchAllItemsByType(ItemType itemType);

// Alle Items mehrerer Typen über alle Seiten abrufen
PaginationHelper.fetchAllItemsByTypes(List<ItemType> itemTypes);

// Markthistorie für ein Item über alle Seiten abrufen
PaginationHelper.fetchAllMarketHistory(String itemId, int tier, MarketType type);

// Gesamtzahl der Seiten für einen Item-Typ abrufen (einzelne Anfrage)
PaginationHelper.getTotalPages(ItemType itemType);

// Gesamtzahl der Items für einen Item-Typ abrufen (einzelne Anfrage)
PaginationHelper.getTotalCount(ItemType itemType);
```

> [!NOTE]
> PaginationHelper-Methoden stellen mehrere API-Anfragen (eine pro Seite). Bei großen Datenmengen kann dies zeitintensiv sein. Der Fortschritt wird automatisch protokolliert.

### 6. Enum-Konvertierungen – Typsichere String-Analyse

Zeichenkettenwerte aus externen Quellen können sicher in Enum-Typen mit Groß-/Kleinschreibungsunabhängiger Übereinstimmung konvertiert werden:

**Beispiel: Item-Typen konvertieren**

```java
// Sichere Konvertierung mit Groß-/Kleinschreibungsunabhängiger Übereinstimmung
ItemType type = ItemType.fromString("SWORD");      // ItemType.SWORD
ItemType type = ItemType.fromString("sword");      // ItemType.SWORD (große/kleine Buchstaben ignoriert)
ItemType type = ItemType.fromString("unknown");    // ItemType.ALL (Standard)

// Lagertyp konvertieren (unterstützt sowohl ID als auch Namen)
LocationType location = LocationType.fromString("1");              // LocationType.BLUEBELL_HOLLOW
LocationType location = LocationType.fromString("bluebell_hollow"); // LocationType.BLUEBELL_HOLLOW
```

**Verfügbare Enum-Konvertierungen:**

```java
ItemType.fromString(String value);           // In ItemType konvertieren, Standard ist ALL
Quality.fromString(String value);             // In Quality konvertieren, Standard ist STANDARD
ClassType.fromString(String value);           // In ClassType konvertieren, Standard ist UNKNOWN
LocationType.fromString(String value);        // In LocationType konvertieren, Standard ist THE_CITADEL
MarketType.fromString(String value);          // In MarketType konvertieren, Standard ist LISTINGS
OnlineStatus.fromString(String value);        // In OnlineStatus konvertieren, Standard ist OFFLINE
MuseumCategory.fromString(String value);      // In MuseumCategory konvertieren, Standard ist SKINS
SkillType.fromString(String value);           // In SkillType konvertieren, Standard ist UNKNOWN
StatType.fromString(String value);            // In StatType konvertieren, Standard ist UNKNOWN
SecondaryStatType.fromString(String value);   // In SecondaryStatType konvertieren, Standard ist UNKNOWN
```

### 7. Response-Dienstprogramme

Die `Response<T>`-Klasse bietet praktische Dienstprogrammmethoden zur Verarbeitung von API-Responses:

**Beispiel: Response-Dienstprogramme**

```java
var response = Requester.getCharacter("id");

// Daten mit einem Standard-Fallback abrufen
CharacterView character = response.orElse(new CharacterView());

// Code bei erfolgreicher Antwort ausführen
response.ifSuccessful(data -> {
    System.out.println("Charakter: " + data.getCharacter().getName());
});

// Code bei fehlgeschlagener Antwort ausführen
response.ifFailed(error -> {
    System.err.println("Fehler: " + error);
});
```

## 📋 Unterstützte API-Endpoints

<details open>
<summary><b>📌 Endpoint-Referenz (50+ Methoden)</b></summary>

<br>

### 🔐 Authentifizierung

| Methode | Beschreibung |
|---------|-------------|
| `getAuthentication()` | Authentifizierungsinformationen mit Token-Rate-Limits abrufen |
| `getAuthentication(String token)` | Authentifizierung mit spezifischem Token prüfen |

### 👤 Charaktere

| Methode | Beschreibung |
|---------|-------------|
| `getCharacter(String id)` | Charakterübersicht abrufen |
| `getCharacterMetrics(String id)` | Charaktermetriken (Stats, Level, Experience) |
| `getCharacterEffects(String id)` | Aktive Effekte eines Charakters |
| `getCharacterAlts(String id)` | Alternative Charaktere eines Spielers |
| `getCharacterMuseum(String id)` | Museum-Sammlungsstatus |
| `getCharacterAction(String id)` | Aktuelle Aktivität des Charakters |
| `getCharacterPets(String id)` | Begleitinformationen |
| `getMultipleCharacters(List<String> ids)` | Mehrere Charaktere parallel abrufen |

### 📦 Gegenstände

| Methode | Beschreibung |
|---------|-------------|
| `searchItems(String query)` | Items nach Name suchen |
| `searchItems(ItemType type)` | Items nach Typ filtern |
| `searchType(ItemType type)` | Alle Items eines Typs abrufen |
| `searchTypes(List<ItemType> types)` | Alle Items mehrerer Typen abrufen |
| `getAllItems()` | Alle Items im Spiel abrufen ⚠️ (Kann mehrere Minuten dauern) |
| `advancedSearchItems(String query)` | Fuzzy-Matching bei der Itemsuche |
| `inspectItem(String id)` | Detaillierte Item-Informationen abrufen |
| `inspectAllItems(boolean cancelOnFailure)` | Alle Item-Details abrufen |
| `getMarketHistory(String id, int tier, MarketType type)` | Markthistorie eines Items |
| `getMultipleItemInspections(List<String> ids)` | Mehrere Item-Details parallel abrufen |

### ⚔️ Gilden

| Methode | Beschreibung |
|---------|-------------|
| `getGuild(int id)` | Gildeninformationen abrufen |
| `getGuildMembers(int id)` | Gildenmitgliederliste abrufen |
| `getCurrentGuildConquest()` | Aktuelle Gildeneroberungen |
| `getGuildConquestBySeason(int season)` | Gildeneroberungen nach Saison |
| `getGuildConquestInspection(LocationType zone)` | Zone-Details der Gildeneroberung |
| `getMultipleGuilds(List<Integer> ids)` | Mehrere Gilden parallel abrufen |
| `getMultipleGuildMembers(List<Integer> ids)` | Mehrere Gildenmitgliederlisten parallel abrufen |

### ⚡ Kampf

| Methode | Beschreibung |
|---------|-------------|
| `getWorldBosses()` | Weltbosse mit Positionen und Stats |
| `getDungeons()` | Verfügbare Dungeons mit Anforderungen |
| `getEnemies()` | Feindliste mit Attributen |

### 🌟 Sonstiges

| Methode | Beschreibung |
|---------|-------------|
| `getCompanionExchangeListings()` | Begleitaus-/eintausch-Angebote |
| `getShrineInfo()` | Heiligtum-Fortschritts-Informationen |

</details>

## 📊 Projektstruktur

<details>
<summary><b>📁 Repository-Layout</b></summary>

```
JIMA/
├── src/main/java/de/shurablack/jima/
│   ├── http/
│   │   ├── RequestManager.java          # HTTP-Verwaltung & Rate-Limiting
│   │   ├── Requester.java               # API-Endpoints (50+ Methoden)
│   │   ├── Response.java                # Response-Wrapper
│   │   ├── Endpoint.java                # Endpoint-Definitionen
│   │   ├── ResponseCode.java            # HTTP-Statuscodes
│   │   └── serialization/
│   │       └── ApiObjectMapper.java     # Jackson-Konfiguration
│   ├── model/                           # Datenmodelle für API-Responses
│   │   ├── auth/                        # Authentifizierungsmodelle
│   │   ├── character/                   # Charakterdaten & Metriken
│   │   ├── combat/                      # Bosse, Dungeons, Feinde
│   │   ├── guild/                       # Gilden & Eroberungsdaten
│   │   ├── item/                        # Item & Marktdaten
│   │   └── ...                          # Zusätzliche Modelle
│   └── util/
│       ├── TokenStore.java              # Thread-sichere Token-Verwaltung
│       ├── PaginationHelper.java         # Abrufen von Mehrseiten-Ergebnissen
│       ├── Configurator.java            # Config-Datei-Verarbeitung
│       ├── ItemNameMatcher.java         # Fuzzy Item-Matching
│       └── types/                       # Enum-Typen (ItemType, ClassType, etc.)
├── src/test/java/de/shurablack/jima/
│   └── ...                              # Unit Tests
├── pom.xml                              # Maven-Konfiguration
├── README.md                            # Englische Dokumentation
└── README.de.md                         # Diese Datei
```

</details>

## 🚀 Best Practices

<details open>
<summary><b>💡 Pro-Tipps & häufige Muster</b></summary>

### Rate Limits handhaben

```java
// Nutzungslimit einstellen, um Anfragen zu sparen
RequestManager.getInstance().setUsageLimit(10);  // Retry bei weniger als 10 verbleibenden Anfragen

// Nutzen Sie rotierende Tokens für höhere Rate Limits
TokenStore.getInstance().loadTokens();
```

### Fehlerbehandlung

```java
var response = Requester.getCharacter("id");
if (!response.isSuccessful()) {
    switch (response.getResponseCode()) {
        case UNAUTHORIZED -> System.out.println("Token ungültig");
        case NOT_FOUND -> System.out.println("Ressource nicht gefunden");
        case RATE_LIMIT_EXCEEDED -> System.out.println("Rate Limit erreicht");
        default -> System.out.println("Fehler: " + response.getError());
    }
}
```

### Große Datenmengen abrufen

```java
// Nutzen Sie searchType/searchTypes Methoden für komplette Item-Listen
// Sie handhaben Pagination automatisch

// Warnung: getAllItems() kann mehrere Minuten dauern
// Nutzen Sie stattdessen gezielt searchType() für spezifische Item-Typen
var response = Requester.searchType(ItemType.WEAPON);
List<Item> weapons = response.getData().getItems();
```

</details>

---

## 📦 Abhängigkeiten

| Paket | Version | Zweck |
|-------|---------|-------|
| [Lombok](https://projectlombok.org/) | Aktuell | Reduziert Boilerplate-Code mit Annotations |
| [Jackson](https://github.com/FasterXML/jackson) | 2.21.1+ | JSON-Serialisierung & Deserialisierung |
| [Log4j2](https://logging.apache.org/log4j/2.x/) | 2.25+ | Erweitertes Logging-Framework |
| [Apache commons-text](https://commons.apache.org/proper/commons-text/) | 1.14.0+ | String-Manipulationsdienstprogramme |
| [JUnit 5](https://junit.org/junit5/) | Aktuell | Test-Framework |

## 📜 Lizenz

Dieses Projekt ist unter der **Apache License 2.0** lizenziert — siehe die [LICENSE](LICENSE) Datei für Details.

---

## 🙏 Danksagungen

**Mit ❤️ von der Community erstellt**

- 🎮 Danke an [Michael Dawson (GalahadXVI)](https://github.com/GalahadXVI) für die Erstellung von **IdleMMO**
- 🤝 Besonderer Dank an die Community für Unterstützung und Feedback
- 📚 Inspiriert von der Idle MMO API-Dokumentation

---

## 📚 Ressourcen & Links

<table>
<tr>
<td width="50%">

### Projekt

- 📖 [Englische Dokumentation](README.md)
- 🇩🇪 [Deutsche Dokumentation](README.de.md) (diese Datei)
- 💻 [GitHub Repository](https://github.com/ShuraBlack/JIMA)
- 📦 [Maven Central](https://mvnrepository.com/artifact/io.github.shurablack/JIMA)

</td>
<td width="50%">

### Spiel & API

- 🌐 [Idle MMO Website](https://web.idle-mmo.com)
- 📖 [Idle MMO Wiki](https://web.idle-mmo.com/wiki)
- 🔑 [API-Token abrufen](https://web.idle-mmo.com/settings/api)
- ⚙️ [API-Dokumentation](https://web.idle-mmo.com/wiki/more/api)

</td>
</tr>
</table>

---

<div align="center">

**[⬆ zurück nach oben](#-jima-java-idle-mmo-api)**

Erstellt mit ☕ von ShuraBlack

</div>
