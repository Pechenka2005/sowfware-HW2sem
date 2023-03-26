import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "me.sj"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.clojars.org")
        name = "Clojars"
    }
}

dependencies {
    implementation("org.mongodb:mongodb-driver-rx:1.5.0")
    implementation("io.reactivex:rxkotlin:1.0.0")
    implementation("io.reactivex:rxnetty-common:0.5.3")
    implementation("io.reactivex:rxnetty-http:0.5.3")
    implementation("io.reactivex:rxnetty-tcp:0.5.3")
    implementation("io.netty:netty-all:4.1.90.Final")
    implementation("junit:junit:4.13.2")
    implementation("org.mockito:mockito-core:5.2.0")
    implementation("clj-bson:clj-bson:0.1.0")
    implementation("org.webjars.npm:bson:1.1.3")

    testImplementation(kotlin("test"))

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}