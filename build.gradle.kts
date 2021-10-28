plugins {
    java
}

subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
        flatDir {
            dir("${rootProject.projectDir}/libs")
        }
    }

    dependencies {
        val junitGroup = "org.junit.jupiter"
        val junitVersion = "5.8.1"
        testImplementation(junitGroup, name = "junit-jupiter-api", junitVersion)
        testRuntimeOnly(junitGroup, name = "junit-jupiter-engine", junitVersion)

        val akkaGroup = "com.typesafe.akka"
        val akkaVersion = "2.6.17"
        implementation(akkaGroup, name = "akka-actor-typed_2.13", akkaVersion)
        testImplementation(akkaGroup, name = "akka-actor-testkit-typed_2.13", akkaVersion)

        implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.6")

        testImplementation(group = "org.assertj", name = "assertj-core", version = "3.21.0")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
