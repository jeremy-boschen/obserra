plugins {
    `java-library`
    id("com.netflix.dgs.codegen") version "8.1.0"
}


repositories {
    mavenCentral()
}


dependencies {
    //DGS BOM/platform dependency
    compileOnly( platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:3.10.2"))

    // no runtime deps—just schema
    compileOnly( "com.graphql-java:graphql-java-extended-scalars")
    compileOnly( "com.fasterxml.jackson.core:jackson-annotations:2.19.1")
}

tasks.generateJava {
    generateClient = false

    packageName         = "org.newtco.obserra.graphql.client"
    subPackageNameTypes = "types"

    typeMapping = mutableMapOf(
        "Url" to "java.net.URL",
        "JSON" to "java.lang.Object"
    )
}
