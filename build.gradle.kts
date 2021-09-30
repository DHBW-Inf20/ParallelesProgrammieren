@file:Suppress("SpellCheckingInspection")

plugins {
    application
    id("io.freefair.lombok") version "6.2.0"
}

application {
    mainClass.set("de.dhbw.parprog.HelloWorld")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(group  = "org.apache.commons" , name = "commons-lang3", version = "3.12.0")
}
