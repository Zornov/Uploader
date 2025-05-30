# Uploader Gradle Plugin

[![License](https://img.shields.io/badge/license-MIT-blue)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.21%2B-blue)](https://kotlinlang.org/)

> A Gradle plugin for uploading artifacts via SFTP, issuing RCON commands, and deploying to Pterodactyl-managed servers via API.

---

## Features

| Feature             | Description                                                      |
|---------------------|------------------------------------------------------------------|
| **SFTP Upload**     | Uploads any local file to a remote server via the SFTP protocol. |
| **RCON Support**    | Executes RCON commands after file upload.                        |
| **Pterodactyl API** | Executes start/stop/restart commands via Pterodactyl API.        |


## Installation

Add the plugin to your `build.gradle.kts`:

```kts
plugins {
    id("dev.zornov.uploader") version "1.3"
}
```
---

## Configuration

Example:

```kotlin
uploader {
    isEnabled = true

    sftp {
        host = ""
        port = 2022
        username = ""
        password = ""
        fileName = "" // File name in build dir
        remote = "server.jar"
    }
   
    pterodactyl {
        isEnabled = true
        apiUrl = ""
        apiKey = ""
        serverId = ""
        commands {
            command("kill")
            command("start")
        }
    }
}
```