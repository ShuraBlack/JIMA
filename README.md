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

**JIMA** is a Java-based API wrapper designed to interact with the Idle MMO API. It provides an intuitive, object-oriented interface to access game data such as characters, guilds, items, dungeons, and more.

---

## 📋 Table of Contents

<details open>
<summary><b>Click to expand/collapse</b></summary>

- [Features](#-features)
- [Quick Start](#-quick-start)
- [Installation](#-installation)
- [Usage](#-usage)
  - [Requester](#1-requester--main-entry-point)
  - [RequestManager](#2-requestmanager--request-management)
  - [TokenStore](#3-tokenstore--token-management)
- [API Reference](#-supported-api-endpoints)
- [Project Structure](#-project-structure)
- [Best Practices](#-best-practices)
- [Dependencies](#-dependencies)
- [License](#-license)

</details>

---

## ⚡ Quick Start

<details>
<summary><b>Get started in 5 minutes</b></summary>

**1. Add dependency to your project:**
```gradle
dependencies {
    implementation 'io.github.shurablack:JIMA:$version'
}
```

**2. Create `jima-config.properties`:**
```properties
API_KEY=your_token_here
CONTACT_EMAIL=your_email@example.com
APPLICATION_VERSION=1.0.0
APPLICATION_NAME=MyApp
```

**3. Start using JIMA:**
```java
// Fetch world bosses
var response = Requester.getWorldBosses();
if (response.isSuccessful()) {
    var bosses = response.getData().getWorldBosses();
    bosses.forEach(boss -> System.out.println(boss.getName()));
}

// Fetch character info
var charResponse = Requester.getCharacter("characterId");
if (charResponse.isSuccessful()) {
    var character = charResponse.getData().getCharacter();
    System.out.println("Level: " + character.getLevel());
}
```

**4. For advanced usage, see [Usage section](#-usage)**

</details>

## ✨ Features

- 🧱 Object-oriented representation of game entities
- 💡 Managed requests without a single line of code
- ⚡ Easy-to-use request methods for seamless data retrieval
- 💾 Built-in configuration management for API keys and application settings
- 🔄 Support for rotating tokens to improve rate limits
- 🌐 Comprehensive support for game features like world bosses, guild conquests, and market history
- 🔒 Secure token management with automatic rate-limit tracking

## 📑 Disclosure

> [!WARNING]
> **This project is not affiliated with IdleMMO.** This is an independent initiative created by the community.

This project is an independent initiative and is not affiliated with or endorsed by the creators of IdleMMO. Please respect the game's terms of service and avoid excessive API requests that may impact server performance.

> **Access token** and **scopes** are managed by the user of this library. Ensure you handle authentication securely and responsibly.

## 🔑 Acquisition of Access token
To obtain an access token for the Idle MMO API, follow these steps:
- Visit [Settings/PublicAPI](https://web.idle-mmo.com/settings/api)
- Click the "Create New Key" button
- Enter the Key Name, Expires in (Days) amd set API Scopes according to your needs
- Click "Generate Key" to create the token
- Copy the generated token and use it in your app configuration

## 🔧 Installation

> ✅ **Requirements:** Java 11+ | Available on Maven Central

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

### Configuration

Create a `jima-config.properties` file:

```properties
API_KEY=<your_token>
CONTACT_EMAIL=<your_email>
APPLICATION_VERSION=<app_version>
APPLICATION_NAME=<app_name>
USE_ROTATING_TOKENS=<true/false>
```

> [!NOTE]
> You can also use environment variables instead of a properties file.

> [!TIP]
> The application auto-generates a config template if it doesn't exist.

> [!IMPORTANT]
> For rotating tokens, create `jima-tokens.txt` with one token per line.

> [!CAUTION]
> Using other players' tokens requires disclosure per [API guidelines](https://web.idle-mmo.com/wiki/more/api). Creating multiple accounts to extend rate limits is not allowed.

## 🧪 Usage

The library provides three main components for API interaction:

### 1. Requester – Main Entry Point

The `Requester` class is the primary interface for API requests. It provides static methods for all supported endpoints.

**Example: Fetching World Bosses**

```java
var response = Requester.getWorldBosses();
if (response.isSuccessful()) {
    WorldBosses bosses = response.getData();
    bosses.getWorldBosses().forEach(boss -> {
        System.out.println("Boss Name: " + boss.getName());
        System.out.println("Location: " + boss.getLocation().getName());
    });
} else {
    System.err.println("Error: " + response.getError());
}
```

**Example: Fetching Character Information**

```java
var response = Requester.getCharacter("hashedCharacterId");
if (response.isSuccessful()) {
    CharacterView view = response.getData();
    System.out.println("Character Name: " + view.getCharacter().getName());
    System.out.println("Class: " + view.getCharacter().getClassType());
} else {
    System.err.println("Error: " + response.getError());
}
```

**Example: Advanced Item Search**

```java
// Fuzzy matching with typo-tolerance
var response = Requester.advancedSearchItems("Wooden Sword");
if (response.isSuccessful()) {
    Items items = response.getData();
    items.getItems().forEach(item -> {
        System.out.println("Item: " + item.getName() + " (ID: " + item.getHashedId() + ")");
    });
}

// Get all items of a specific type
var response = Requester.searchType(ItemType.WEAPON);
```

### 2. RequestManager – Request Management

The `RequestManager` is a singleton that handles all HTTP communication with rate-limiting and automatic retry. You typically don't need to use it directly, as `Requester` uses it automatically.

**Automatic Features:**

- **Rate-Limiting**: When remaining requests fall below a configured minimum, retries are automatically scheduled
- **Asynchronous Processing**: All requests are asynchronous using `CompletableFuture`
- **Error Handling**: Automatic logging and error handling with Log4j2
- **Configurable**: Log level and usage limits can be adjusted at runtime

**Optional Configuration:**

```java
// Set log level
RequestManager.setLogLevel(Level.DEBUG);

// Set usage limit (retry when less than X requests remaining)
RequestManager.getInstance().setUsageLimit(5);
```

### 3. TokenStore – Token Management

The `TokenStore` is a thread-safe singleton for managing multiple API tokens. This is especially useful for higher rate limits.

**Features:**

- **Token Rotation**: Tokens are sorted by remaining requests (descending)
- **Rate-Limit Tracking**: Each token is managed with remaining requests and reset time
- **Thread-Safe**: Uses `ConcurrentSkipListSet` for safe multi-threaded operations
- **Auto-Loading**: Has a `loadTokens()` method to load from `jima-tokens.txt`

**Example: Using Rotating Tokens**

```java
// Load tokens (from jima-tokens.txt)
TokenStore.getInstance().loadTokens();

// Add a token
TokenStore.getInstance().addToken("your_api_token");

// Get authentications of all stored tokens
List<Authentication> auths = TokenStore.getInstance().getTokenAuthentications();
auths.forEach(auth -> {
    System.out.println("Username: " + auth.getAccount().getUsername());
    System.out.println("Remaining Requests: " + auth.getRateLimitRemaining());
});
```

> [!TIP]
> - The `ApiObjectMapper` defines Jackson modules and an ObjectMapper for serialization/deserialization.<br>
> - The `ImageLoader` is a convenience class for loading images from URLs.<br>
> - API calls are blocking by default (`join()` on `CompletableFuture`), but can be used asynchronously for better performance.

### 4. Batch Operations – Parallel Requests

For improved performance when making multiple requests, use the batch operation methods that execute requests in parallel:

**Example: Fetching Multiple Characters**

```java
List<String> characterIds = Arrays.asList("id1", "id2", "id3");
List<Response<CharacterView>> responses = Requester.getMultipleCharacters(characterIds);

// Filter successful responses
List<CharacterView> characters = responses.stream()
    .filter(Response::isSuccessful)
    .map(Response::getData)
    .collect(Collectors.toList());
```

**Available Batch Methods:**

```java
// Fetch multiple characters in parallel
Requester.getMultipleCharacters(List<String> characterIds);

// Fetch multiple guilds in parallel
Requester.getMultipleGuilds(List<Integer> guildIds);

// Fetch multiple guild member lists in parallel
Requester.getMultipleGuildMembers(List<Integer> guildIds);

// Fetch multiple item inspections in parallel
Requester.getMultipleItemInspections(List<String> itemIds);
```

### 5. PaginationHelper – Automatic Multi-Page Fetching

For endpoints that return paginated results, the `PaginationHelper` utility automatically handles fetching all pages and combining results:

**Example: Fetching All Items of a Type**

```java
// Automatically fetches all pages and combines results
List<Item> swords = PaginationHelper.fetchAllItemsByType(ItemType.SWORD);
System.out.println("Total swords: " + swords.size());

// Fetch multiple item types
List<ItemType> armorTypes = Arrays.asList(
    ItemType.CHESTPLATE, ItemType.HELMET, ItemType.BOOTS, ItemType.GREAVES
);
List<Item> armorItems = PaginationHelper.fetchAllItemsByTypes(armorTypes);
```

**Available PaginationHelper Methods:**

```java
// Fetch all items of a specific type across all pages
PaginationHelper.fetchAllItemsByType(ItemType itemType);

// Fetch all items of multiple types across all pages
PaginationHelper.fetchAllItemsByTypes(List<ItemType> itemTypes);

// Fetch market history for an item across all pages
PaginationHelper.fetchAllMarketHistory(String itemId, int tier, MarketType type);

// Get total number of pages for an item type (single request)
PaginationHelper.getTotalPages(ItemType itemType);

// Get total item count for an item type (single request)
PaginationHelper.getTotalCount(ItemType itemType);
```

> [!NOTE]
> PaginationHelper methods make multiple API requests (one per page). For large datasets, this can take significant time. Progress is logged automatically.

### 6. Enum Conversions – Type-Safe String Parsing

String values from external sources can be safely converted to enum types with case-insensitive matching:

**Example: Converting Item Types**

```java
// Safe conversion with case-insensitive matching
ItemType type = ItemType.fromString("SWORD");      // ItemType.SWORD
ItemType type = ItemType.fromString("sword");      // ItemType.SWORD (case-insensitive)
ItemType type = ItemType.fromString("unknown");    // ItemType.ALL (default)

// Convert location type (supports both ID and name)
LocationType location = LocationType.fromString("1");              // LocationType.BLUEBELL_HOLLOW
LocationType location = LocationType.fromString("bluebell_hollow"); // LocationType.BLUEBELL_HOLLOW
```

**Available Enum Conversions:**

```java
ItemType.fromString(String value);           // Converts to ItemType, defaults to ALL
Quality.fromString(String value);             // Converts to Quality, defaults to STANDARD
ClassType.fromString(String value);           // Converts to ClassType, defaults to UNKNOWN
LocationType.fromString(String value);        // Converts to LocationType, defaults to THE_CITADEL
MarketType.fromString(String value);          // Converts to MarketType, defaults to LISTINGS
OnlineStatus.fromString(String value);        // Converts to OnlineStatus, defaults to OFFLINE
MuseumCategory.fromString(String value);      // Converts to MuseumCategory, defaults to SKINS
SkillType.fromString(String value);           // Converts to SkillType, defaults to UNKNOWN
StatType.fromString(String value);            // Converts to StatType, defaults to UNKNOWN
SecondaryStatType.fromString(String value);   // Converts to SecondaryStatType, defaults to UNKNOWN
```

### 7. Response Utilities

The `Response<T>` class provides convenient utility methods for handling API responses:

**Example: Response Utilities**

```java
var response = Requester.getCharacter("id");

// Get data with a default fallback
CharacterView character = response.orElse(new CharacterView());

// Execute code on successful response
response.ifSuccessful(data -> {
    System.out.println("Character: " + data.getCharacter().getName());
});

// Execute code on failed response
response.ifFailed(error -> {
    System.err.println("Error: " + error);
});
```

## 📋 Supported API Endpoints

<details open>
<summary><b>📌 Endpoint Reference (50+ methods)</b></summary>

<br>

### 🔐 Authentication

| Method | Description |
|--------|-------------|
| `getAuthentication()` | Get authentication info with token rate limits |
| `getAuthentication(String token)` | Check authentication with specific token |

### 👤 Characters

| Method | Description |
|--------|-------------|
| `getCharacter(String id)` | Get character overview |
| `getCharacterMetrics(String id)` | Get character metrics (stats, level, experience) |
| `getCharacterEffects(String id)` | Get active effects on a character |
| `getCharacterAlts(String id)` | Get alternative characters of a player |
| `getCharacterMuseum(String id)` | Get museum collection status |
| `getCharacterAction(String id)` | Get current character activity |
| `getCharacterPets(String id)` | Get companion information |
| `getMultipleCharacters(List<String> ids)` | Get multiple characters in parallel |

### 📦 Items

| Method | Description |
|--------|-------------|
| `searchItems(String query)` | Search items by name |
| `searchItems(ItemType type)` | Filter items by type |
| `searchType(ItemType type)` | Get all items of a type |
| `searchTypes(List<ItemType> types)` | Get all items of multiple types |
| `getAllItems()` | Get all items in game ⚠️ (Can take several minutes) |
| `advancedSearchItems(String query)` | Fuzzy matching item search |
| `inspectItem(String id)` | Get detailed item information |
| `inspectAllItems(boolean cancelOnFailure)` | Get all item details |
| `getMarketHistory(String id, int tier, MarketType type)` | Get item market history |
| `getMultipleItemInspections(List<String> ids)` | Get multiple item details in parallel |

### ⚔️ Guilds

| Method | Description |
|--------|-------------|
| `getGuild(int id)` | Get guild information |
| `getGuildMembers(int id)` | Get guild member list |
| `getCurrentGuildConquest()` | Get current guild conquests |
| `getGuildConquestBySeason(int season)` | Get guild conquests by season |
| `getGuildConquestInspection(LocationType zone)` | Get zone details for guild conquest |
| `getMultipleGuilds(List<Integer> ids)` | Get multiple guilds in parallel |
| `getMultipleGuildMembers(List<Integer> ids)` | Get multiple guild member lists in parallel |

### ⚡ Combat

| Method | Description |
|--------|-------------|
| `getWorldBosses()` | Get world bosses with positions and stats |
| `getDungeons()` | Get available dungeons with requirements |
| `getEnemies()` | Get enemy list with attributes |

### 🌟 Other

| Method | Description |
|--------|-------------|
| `getCompanionExchangeListings()` | Get companion exchange/trade listings |
| `getShrineInfo()` | Get shrine progress information |

</details>

## 📊 Project Structure

<details>
<summary><b>📁 Repository Layout</b></summary>

```
JIMA/
├── src/main/java/de/shurablack/jima/
│   ├── http/
│   │   ├── RequestManager.java          # HTTP management & rate-limiting
│   │   ├── Requester.java               # API endpoints (50+ methods)
│   │   ├── Response.java                # Response wrapper
│   │   ├── Endpoint.java                # Endpoint definitions
│   │   ├── ResponseCode.java            # HTTP status codes
│   │   └── serialization/
│   │       └── ApiObjectMapper.java     # Jackson configuration
│   ├── model/                           # Data models for API responses
│   │   ├── auth/                        # Authentication models
│   │   ├── character/                   # Character data & metrics
│   │   ├── combat/                      # Bosses, dungeons, enemies
│   │   ├── guild/                       # Guild & conquest data
│   │   ├── item/                        # Item & market data
│   │   └── ...                          # Additional models
│   └── util/
│       ├── TokenStore.java              # Thread-safe token management
│       ├── PaginationHelper.java         # Multi-page result fetching
│       ├── Configurator.java            # Config file parsing
│       ├── ItemNameMatcher.java         # Fuzzy item matching
│       └── types/                       # Enum types (ItemType, ClassType, etc.)
├── src/test/java/de/shurablack/jima/
│   └── ...                              # Unit tests
├── pom.xml                              # Maven configuration
├── README.md                            # This file (English)
└── README.de.md                         # German documentation
```

</details>

## 🚀 Best Practices

<details open>
<summary><b>💡 Pro Tips & Common Patterns</b></summary>

### Handle Rate Limits

```java
// Configure usage limit to save requests
RequestManager.getInstance().setUsageLimit(10);  // Retry when less than 10 requests remaining

// Use rotating tokens for higher rate limits
TokenStore.getInstance().loadTokens();
```

### Error Handling

```java
var response = Requester.getCharacter("id");
if (!response.isSuccessful()) {
    switch (response.getResponseCode()) {
        case UNAUTHORIZED -> System.out.println("Invalid token");
        case NOT_FOUND -> System.out.println("Resource not found");
        case RATE_LIMIT_EXCEEDED -> System.out.println("Rate limit exceeded");
        default -> System.out.println("Error: " + response.getError());
    }
}
```

### Fetching Large Data Sets

```java
// Use searchType/searchTypes methods for complete item lists
// They handle pagination automatically

// Warning: getAllItems() can take several minutes
// Use searchType() for specific item types instead
var response = Requester.searchType(ItemType.WEAPON);
List<Item> weapons = response.getData().getItems();
```

</details>

---
## 📦 Dependencies

| Package | Version | Purpose |
|---------|---------|---------|
| [Lombok](https://projectlombok.org/) | Latest | Reduces boilerplate code with annotations |
| [Jackson](https://github.com/FasterXML/jackson) | 2.21.1+ | JSON serialization & deserialization |
| [Log4j2](https://logging.apache.org/log4j/2.x/) | 2.25+ | Advanced logging framework |
| [Apache commons-text](https://commons.apache.org/proper/commons-text/) | 1.14.0+ | String manipulation utilities |
| [JUnit 5](https://junit.org/junit5/) | Latest | Testing framework |

## 📜 License
This project is licensed under the **Apache License 2.0** — see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

**Created with ❤️ by the community**

- 🎮 Thanks to [Michael Dawson (GalahadXVI)](https://github.com/GalahadXVI) for creating **IdleMMO**
- 🤝 Special thanks to the community for support and feedback
- 📚 Inspired by the Idle MMO API documentation

---

## 📚 Resources & Links

<table>
<tr>
<td width="50%">

### Project

- 📖 [English Docs](README.md) (this file)
- 🇩🇪 [German Docs](README.de.md)
- 💻 [GitHub Repository](https://github.com/ShuraBlack/JIMA)
- 📦 [Maven Central](https://mvnrepository.com/artifact/io.github.shurablack/JIMA)

</td>
<td width="50%">

### Game & API

- 🌐 [Idle MMO Website](https://web.idle-mmo.com)
- 📖 [Idle MMO Wiki](https://web.idle-mmo.com/wiki)
- 🔑 [Get API Token](https://web.idle-mmo.com/settings/api)
- ⚙️ [API Documentation](https://web.idle-mmo.com/wiki/more/api)

</td>
</tr>
</table>

---

<div align="center">

**[⬆ back to top](#-jima-java-idle-mmo-api)**

Made with ☕ by ShuraBlack

</div>

