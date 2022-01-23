plugins {
    id("net.kyori.blossom")
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

blossom {
    replaceToken("\$VERSION", project.version)
}
