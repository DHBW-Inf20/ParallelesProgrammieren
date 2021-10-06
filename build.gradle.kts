subprojects {
    apply(plugin = "application")

    repositories {
        mavenCentral()
        flatDir {
            dir("${rootProject.projectDir}/libs")
        }
    }
}
