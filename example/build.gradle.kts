import java.security.MessageDigest

plugins {
    kotlin("jvm")
    id("dev.zornov.uploader") version "1.0"
}

repositories {
    mavenCentral()
}

val gameDir = "test"
val tmuxSessionId = "120"

uploader {
    sftp {
        host = "0.0.0.0"
        username = "zorin"
        privateKeyPath = "/home/user/.ssh/id_rsa"
    }
    ssh {
        host = "0.0.0.0"
        username = "zorin"
        privateKeyPath = "/home/user/.ssh/id_rsa"
    }
    action {
        suspend fun uploadIfChanged(localFile: File, remotePath: String) {
            val localHash = localFile.inputStream().use { it ->
                MessageDigest.getInstance("SHA-256")
                    .digest(it.readBytes())
                    .joinToString("") { "%02x".format(it) }
            }

            val remoteHash = ssh.execute("test -f $remotePath && sha256sum $remotePath | awk '{print \$1}' || echo false").trim()

            if (remoteHash == "false") {
                println("Remote file does not exist, uploading: $remotePath")
                sftp.upload(localFile.absolutePath, remotePath)
                return
            }

            val changed = remoteHash.isEmpty() || remoteHash != localHash
            println("Remote file exists, changed: $changed")
            if (changed) {
                println("Uploading changed file: ${localFile.name} to $remotePath")
                sftp.upload(localFile.absolutePath, remotePath)
            } else {
                println("${localFile.name} unchanged, skipping upload")
            }
        }

        val buildDir = project.layout.buildDirectory.asFile.get()
        val libsDir = File(buildDir, "libs")

        File(libsDir, "dependencies").listFiles()?.forEach { file ->
            if (file.isFile && file.extension == "jar") {
                uploadIfChanged(file, "$gameDir/libraries/${file.name}")
            }
        }

        libsDir.listFiles()?.forEach { file ->
            if (file.isFile && file.extension == "jar") {
                uploadIfChanged(file, "$gameDir/plugins/${file.name}")
            }
        }

        ssh.execute("tmux send-keys -t $tmuxSessionId stop Enter")
        println("Deployment finished")
    }
}

kotlin {
    jvmToolchain(21)
}