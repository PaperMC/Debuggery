plugins {
    id("io.github.goooler.shadow")
    id("xyz.jpenilla.run-velocity") version "2.1.0"
}

tasks {
    val shadowJar = named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveClassifier.set("")
        from(project.rootProject.file("LICENSE.md"))
    }

    build {
        dependsOn(shadowJar)
    }
}

dependencies {
    implementation(project(":debuggery-common"))
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
}
