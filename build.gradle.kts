plugins {
    base
    `java-library`
    id("com.gradleup.shadow") apply false
}

allprojects {
    apply(plugin = "java")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
}

subprojects {
    tasks {
        withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(17)
        }
        withType<Javadoc> {
            options.encoding = Charsets.UTF_8.name()
        }
        processResources {
            filteringCharset = Charsets.UTF_8.name()
        }
        test {
            useJUnitPlatform()
        }
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:24.0.1")
        testImplementation("org.jetbrains:annotations:24.0.1")

        testImplementation(platform("org.junit:junit-bom:6.0.2"))
        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}
