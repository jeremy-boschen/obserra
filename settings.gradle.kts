rootProject.name = "obserra"


println("Gradle version: ${gradle.gradleVersion}")
println("Java version:   ${JavaVersion.current()}")
println("JAVA_HOME:      ${System.getenv("JAVA_HOME")}")
println("GRADLE_HOME:    ${System.getenv("GRADLE_HOME")}")


fun includeProject(path: String) {
    val dir = file(path)
    val name = ":${dir.name}"

    settings.include(name)

    val prj = project(name)
    prj.projectDir = dir
}

includeProject("modules/obserra-graphql")
includeProject("modules/obserra-shared")
includeProject("modules/obserra-backend")
includeProject("modules/obserra-spring-boot-starter")
includeProject("modules/obserra-samples/demo-app")
