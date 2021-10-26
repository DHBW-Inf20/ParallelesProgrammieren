@file:Suppress("SpellCheckingInspection")

plugins {
    application
}

dependencies {
    val akkaGroup = "com.typesafe.akka"
    val akkaVersion = "2.6.17"
    implementation(akkaGroup, name = "akka-actor-typed_2.13", akkaVersion)
    testImplementation(akkaGroup, name = "akka-actor-testkit-typed_2.13", akkaVersion)

    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.6")
}

application {
    mainClass.set("de.dhbw.parprog.ActorCalculation")
}
