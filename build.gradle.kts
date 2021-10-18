subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
        flatDir {
            dir("${rootProject.projectDir}/libs")
        }
    }
}
