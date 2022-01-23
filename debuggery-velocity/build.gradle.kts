plugins {
    id("net.kyori.blossom")
    id("com.github.johnrengelman.shadow")
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
    compileOnly("com.velocitypowered:velocity-api:3.1.0")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.0")
}

blossom {
    replaceToken("\$VERSION", project.version)
}
