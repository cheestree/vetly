plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.asciidoctor)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.ktlint)
    jacoco
}

group = "com.cheestree"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
    implementation(libs.spring.boot.jpa)
    implementation(libs.spring.boot.web)
    implementation(libs.spring.boot.webflux)
    implementation(libs.spring.boot.validation)
    implementation(libs.spring.boot.security)
    implementation(libs.spring.boot.redis)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.reactor.extensions)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.coroutines.reactor)
    implementation(libs.firebase.admin)
    implementation(libs.jjwt.api)
    implementation(libs.jjwt.jackson)
    implementation(libs.springdoc.webmvc)
    implementation(libs.springdoc.ui)
    implementation(libs.tika.core)
    implementation(libs.tika.parsers)
    implementation(libs.jackson.nullable)
    implementation(libs.hypersistence)

    developmentOnly(libs.spring.boot.devtools)

    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.h2)

    testImplementation(libs.embedded.redis)
    testImplementation(libs.spring.boot.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.reactor.test)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.spring.restdocs)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)

    testRuntimeOnly(libs.junit.platform.launcher)
}

configurations.implementation {
    exclude("commons-logging")
}

configurations.all {
    exclude(group = "org.slf4j", module = "slf4j-simple")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<Test>("test") {
    outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)
}

ktlint {
    version.set(libs.versions.ktlint.asProvider())
}

tasks.check {
    dependsOn("jacocoTestReport")
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
