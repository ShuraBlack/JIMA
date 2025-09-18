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

## ✨ Features

- 🧱 Object-oriented representation of game entities
- 💡 Managed requests without a single line of code
- ⚡ Easy-to-use request methods for seamless data retrieval
- 💾 Built-in configuration management for API keys and application settings
- 🔍 Support for various game features like world bosses, guild conquests, and market history

## 📑 Disclosure
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
[![Version][]][Version]
- This library requires **Java 11** or higher. This library is available on maven central.
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
- Create a `jima-config.properties` file next to your build or inside your project folder with the following content:
> [!NOTE]  
> You can also use env variables instead of a properties file

> [!TIP]  
> The application will auto generate a `jima-config.properties` template file if it does not exist

```
API_KEY=<your_token>
CONTACT_EMAIL=<your_email>
APPLICATION_VERSION=<app_version>
APPLICATION_NAME=<app_name>
USE_ROTATING_TOKENS=<true/false>
```

> [!IMPORTANT]  
> For Rotating tokens, you will need an additional file `jima-tokens.txt` with one token per line

> [!CAUTION]
> Inserting the tokens of other players requires disclosing like written [here](https://web.idle-mmo.com/wiki/more/api).<br>
> It is not allowed to create multiple accounts to extend the rate limit.

## 🧪 Usage

> The `Requester` or `ParallelRequester` class is the main entry point for making API requests.

Example: Fetching World Bosses

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

Example: Fetching Character Information

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
> [!TIP]
> - The `ApiObjectMapper` defines simple modules as well as an ObjectMapper that can be used to serialize/deserialize objects.<br>
> - The `ImageLoader` is a convenience class for loading images from URLs.<br>
> - You can get the Authentication of all stored tokens by calling `TokenStore.getTokenAuthentications()`.

## 📦 Dependencies
[Lombok](https://projectlombok.org/) — Simplifies Java code with annotations. <br>
[Jackson](https://github.com/FasterXML/jackson) — JSON serialization and deserialization. <br>
[Log4j2](https://logging.apache.org/log4j/2.x/) — Advanced logging framework. <br>
[Apache commons-text](https://commons.apache.org/proper/commons-text/) — String manipulation utilities. <br>

## 📜 License
This project is licensed under the Apache License 2.0.
See the LICENSE file for more details.

## 🙏 Acknowledgments
Thanks to the creators of the IdleMMO game ([Michael Dawson aka. GalahadXVI](https://github.com/GalahadXVI)). <br>
Special thanks to the community for their support and feedback ❤️.
