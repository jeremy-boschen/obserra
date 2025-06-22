rootProject.name = "obserra"


println("Gradle version: ${gradle.gradleVersion}")
println("Java version:   ${JavaVersion.current()}")
println("JAVA_HOME:      ${System.getenv("JAVA_HOME")}")
println("GRADLE_HOME:    ${System.getenv("GRADLE_HOME")}")


fun includeProject(path: String) {
    val dir = file("modules/$path")
    val name = ":${dir.name}"

    settings.include(name)

    val prj = project(name)
    prj.projectDir = dir
}

includeProject("graphql")
includeProject("obserra-shared")
includeProject("obserra-backend")
includeProject("obserra-spring-boot-starter")
includeProject("obserra-samples/demo-app")
