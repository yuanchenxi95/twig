import com.google.protobuf.gradle.* // ktlint-disable no-wildcard-imports
import org.gradle.kotlin.dsl.proto
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

val grpcVersion = "1.40.0"
val protobufJavaVersion = "3.17.2"
val packageName = "com/yuanchenxi95/twig"
val mainClass = "$packageName/TwigApplicationKt"

buildscript {
    repositories {
        google()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        val kotlinVersion = "1.3.20"
        val springBootVersion = "2.4.5.RELEASE"
        val protobufVersion = "0.8.16"
        classpath("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-noarg:$kotlinVersion")
        classpath("com.google.protobuf:protobuf-gradle-plugin:$protobufVersion")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:10.0.0")
    }
}

repositories {
    // Required to download KtLint
    mavenCentral()
}

plugins {
    id("org.springframework.boot") version "2.4.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.google.protobuf") version "0.8.16"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
//    id("org.jlleitschuh.gradle.ktlint-idea") version "10.0.0"
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.spring") version "1.4.32"
    application
    java
    idea
}

application {
    mainClass.set("com/yuanchenxi95/twig/TwigApplicationKt")
}

group = "com.yuanchenxi95"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    compile("io.projectreactor.kotlin", "reactor-kotlin-extensions")
    compile("org.springframework.boot", "spring-boot-starter-thymeleaf")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("com.google.protobuf", "protobuf-java", protobufJavaVersion)
    implementation("com.google.protobuf", "protobuf-java-util", protobufJavaVersion)
    implementation("com.google.protobuf", "protobuf-kotlin", protobufJavaVersion)
    implementation("commons-validator", "commons-validator", "1.7")
    implementation("dev.miku:r2dbc-mysql")
    implementation("io.grpc", "grpc-protobuf", grpcVersion)
    implementation("io.grpc", "grpc-stub", grpcVersion)
    implementation("io.sentry:sentry-spring-boot-starter:4.3.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot", "spring-boot-configuration-processor")
    implementation("org.springframework.boot", "spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot", "spring-boot-starter-security")
    implementation("org.springframework.boot", "spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation(kotlin("stdlib"))
    runtimeOnly("mysql:mysql-connector-java")
    testCompile("com.google.truth.extensions", "truth-proto-extension", "1.1.2")
    testCompile("io.projectreactor", "reactor-test")
    testImplementation("io.projectreactor", "reactor-test")
    testImplementation("junit", "junit", "4.13.1")
    testImplementation("org.assertj", "assertj-core", "3.19.0")
    testImplementation("org.mockito", "mockito-inline", "3.11.2")
    testImplementation("org.springframework.boot", "spring-boot-starter-test")
    testImplementation("org.springframework.security", "spring-security-test")
    testImplementation("org.testcontainers", "mysql", "1.15.3")
    testImplementation("org.testcontainers", "r2dbc", "1.15.3")
    testImplementation("org.testcontainers", "testcontainers", "1.15.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufJavaVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        id("kotlin")
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc") {
                    outputSubDir = "grpc"
                }
                id("kotlin")
            }
            task.generateDescriptorSet = true
            task.outputs.upToDateWhen { false }
        }
    }
}

val generatedJava = file("${protobuf.protobuf.generatedFilesBaseDir}/main/java")
val generatedGrpc = file("${protobuf.protobuf.generatedFilesBaseDir}/main/grpc")

sourceSets {
    main {
        proto {
            // In addition to the default 'src/main/proto'
            srcDir("protobuf")
            srcDir("src/main/protobuf")
        }
        java {
            srcDirs(
                generatedJava,
                generatedGrpc
            )
        }
    }
    test {
        proto {
            // In addition to the default 'src/test/proto'
            srcDir("src/test/protobuf")
        }
    }
}

idea {
    module {
        generatedSourceDirs.add(generatedJava)
        generatedSourceDirs.add(generatedGrpc)
    }
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    reporters {
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.JSON)
        reporter(ReporterType.HTML)
    }
    filter {
        exclude("**/style-violations.kt")
    }
}
