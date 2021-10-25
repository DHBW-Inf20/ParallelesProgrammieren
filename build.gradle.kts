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

        testImplementation(junitGroup, "junit-jupiter-api", junitVersion)
        testRuntimeOnly(junitGroup, "junit-jupiter-engine", junitVersion)
        testImplementation(group = "org.assertj", name = "assertj-core", version = "3.21.0")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
