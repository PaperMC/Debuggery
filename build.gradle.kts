plugins {
    base
    `java-library`
    id("io.github.goooler.shadow") version "8.1.7" apply false
}

allprojects {
    apply(plugin = "java")
    group = "io.papermc.debuggery"
    version = "1.5.2-SNAPSHOT"

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }
}

subprojects {
    tasks {
        withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(21)
        }
        withType<Javadoc> {
            options.encoding = Charsets.UTF_8.name()
        }
        processResources {
            filteringCharset = Charsets.UTF_8.name()
        }
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:24.0.1")
        testImplementation("org.jetbrains:annotations:24.0.1")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    }
}
