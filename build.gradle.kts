plugins {
    application
}

group = "com.github.samcarlberg"
version = "0.1.1"

repositories {
    mavenCentral()
}

dependencies {
    // None :)
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClassName = "com.github.samcarlberg.colorburst.Colorburst"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = project.application.mainClassName
    }
}
