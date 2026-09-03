import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    application
    jacoco
    checkstyle
    alias(libs.plugins.lombok)
    alias(libs.plugins.versions)
    alias(libs.plugins.spotless)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.shadow)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.openapi.generator)
}

group = "io.hexlet.blog"
version = "1.0-SNAPSHOT"

application {
    // mainClass.set("io.hexlet.blog.Application")
    mainClass = "io.hexlet.cv.App"
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") } // нужен для inertia4j
}

dependencies {
    // Spring Boot
    implementation(libs.springBootStarterWeb)
    implementation(libs.springBootStarterDataJpa)
    implementation(libs.springBootStarterValidation)
    implementation(libs.springBootStarterActuator)
    implementation(libs.springBootStarterSecurity)
    implementation(libs.springBootStarterOauth2ResourceServer)
    implementation(libs.springBootDevtools)
    implementation(libs.springBootConfigProcessor)

    // OpenAPI
    implementation(libs.springdocOpenapiUi)

    // Utilities
    implementation(libs.jacksonDatabindNullable)
    implementation(libs.commonsLang3)
    implementation(libs.datafaker)
    implementation(libs.instancioJunit)
    implementation(libs.jsonunitAssertj)
    implementation(libs.guava)
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")

    // MapStruct
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstructProcessor)

    // DB
    runtimeOnly(libs.h2)
    implementation(libs.postgresql);
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // Tests
    testImplementation(libs.springBootStarterTest)
    testImplementation(libs.springSecurityTest)
    testImplementation(platform(libs.junitBom))
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)

    // Inertia4J
    implementation(libs.inertia4jSpring)
    // implementation(libs.inertia4jSpringStarter)

    // Jackson JSR310 для поддержки LocalDateTime
    implementation(libs.jacksonDatatypeJsr310)

}

tasks.test {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events =
            setOf(
                TestLogEvent.FAILED,
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED,
            )
        showStandardStreams = true
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

spotless {
    java {
        importOrder()
        removeUnusedImports()
// убрал так как методы выстраивает в цепочки и конфликтует в checkstyle
// в комментариях рушит отступы заменя на *
        //eclipse().sortMembersEnabled(true)
// убрал форматирование аннотаций так как при выстраивании в одну строку
// строка получается слишком длинной и конфликтует в checkstyle
       // formatAnnotations()
        leadingTabsToSpaces(4)
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

// sonar {
//     properties {
//         property("sonar.projectKey", "hexlet-boilerplates_java-package")
//         property("sonar.organization", "hexlet-boilerplates")
//         property("sonar.host.url", "https://sonarcloud.io")
//     }
// }

tasks.register<Exec>("installTypeSpecDependencies") {
    workingDir("api")
    commandLine("npm", "ci")
}

tasks.register<Exec>("compileTypeSpec") {
    dependsOn(tasks.named("installTypeSpecDependencies"))
    commandLine("npm", "--prefix", "api", "run", "compile")
}

tasks.openApiGenerate {
    dependsOn(tasks.named("compileTypeSpec"))
    generatorName.set("spring")
    inputSpec.set(layout.buildDirectory.file("openapi/openapi.yaml").get().asFile.path)
    outputDir.set(layout.buildDirectory.dir("generated-sources/openapi"))
    apiPackage.set("io.hexlet.cv.controller")
    modelPackage.set("io.hexlet.cv.dto")
    configOptions.set(
        mapOf(
            "interfaceOnly" to "true",
            "useSpringBoot3" to "true",
            "openApiNullable" to "false",
            "useRecords" to "true",
            "useTags" to "true"
        ),
    )
}

tasks.compileJava {
    dependsOn(tasks.openApiGenerate)
}

sourceSets {
    main {
        java.srcDir(layout.buildDirectory.dir("generated-sources/openapi/src/main/java"))
    }
}
