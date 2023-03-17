dependencyResolutionManagement {
    repositories {
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        mavenCentral()
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        id("net.kyori.blossom") version "1.2.0"
        id("com.github.johnrengelman.shadow") version "8.1.0"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

rootProject.name = "debuggery"
include("debuggery-common")
include("debuggery-bukkit")
include("debuggery-velocity")
