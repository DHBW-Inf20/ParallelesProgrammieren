@file:Suppress("SpellCheckingInspection")

plugins {
    application
}

dependencies {
    implementation(":processemu")
}

application {
    mainClass.set("de.dhbw.parprog.JavaPipe")
}
