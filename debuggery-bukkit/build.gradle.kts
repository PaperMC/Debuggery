plugins {
    id("io.github.goooler.shadow")
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

tasks {
    processResources {
        filesMatching("paper-plugin.yml") {
            expand("version" to project.version)
        }
    }

    val shadowJar = named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveClassifier.set("")
        from(project.rootProject.file("LICENSE.md"))
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion("1.20.6")
    }
}

dependencies {
    implementation(project(":debuggery-common"))
    compileOnly("io.papermc.paper:paper-api:1.20.5-R0.1-SNAPSHOT")
    testImplementation(project(path = ":debuggery-common", configuration = "testArchive"))
    testImplementation("io.papermc.paper:paper-api:1.20.5-R0.1-SNAPSHOT")
}
