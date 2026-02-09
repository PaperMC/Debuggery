dependencyResolutionManagement {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
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
        id("net.kyori.blossom") version "2.2.0"
        id("com.gradleup.shadow") version "9.3.1"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "debuggery"
include("debuggery-common")
include("debuggery-bukkit")
include("debuggery-velocity")
