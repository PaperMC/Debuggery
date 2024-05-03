plugins {
    id("net.kyori.blossom")
}

dependencies {
    compileOnly("net.kyori:adventure-api:4.16.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.16.0")
}

configurations.register("testArchive") {
    extendsFrom(configurations.testCompileOnly.get())
}
tasks.register<Jar>(name = "jarTest") {
    from(project.sourceSets.test.get().output)
    description = "create a jar from the test source set"
    archiveClassifier.set("test")
}

artifacts {
    add("testArchive", tasks.getByName("jarTest"))
}

sourceSets {
    main {
        blossom {
            resources {
                property("\$VERSION", project.version.toString())
            }
        }
    }
}
