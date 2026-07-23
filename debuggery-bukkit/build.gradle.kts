plugins {
    id("com.gradleup.shadow")
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

tasks {
    processResources {
        expand("version" to project.version)
    }

    val shadowJar = named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveClassifier.set("")
        from(project.rootProject.file("LICENSE.md"))
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion("26.2")
    }
}

dependencies {
    implementation(project(":debuggery-common"))
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    testImplementation(project(path = ":debuggery-common", configuration = "testArchive"))
    testImplementation("io.papermc.paper:paper-api:26.2.build.+")
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(25)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
}
