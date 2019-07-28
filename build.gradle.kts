import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.41"
}

application {
    mainClassName = "com.github.daggerok.akkakotlinexample.MainKt"
}

defaultTasks("clean", "build", "run")

group = "com.github.daggerok.akkakotlinexample"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.typesafe.akka:akka-actor_2.13:2.6.0-M5")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
