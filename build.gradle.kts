plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    kotlin("plugin.jpa") version "2.2.21"
    `java-library`
    `java-test-fixtures`
    `maven-publish`
}

group = "com.patryklikus"
version = "0.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

extra["springBootVersion"] = "4.0.3"
extra["springModulithVersion"] = "2.0.3"

dependencies {
    compileOnly(platform("org.springframework.boot:spring-boot-dependencies:${property("springBootVersion")}"))
    compileOnly(platform("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}"))

    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-webmvc")
    compileOnly("jakarta.servlet:jakarta.servlet-api")
    compileOnly("org.springframework:spring-orm")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    compileOnly("org.springframework.boot:spring-boot-hibernate")
    compileOnly("org.springframework.data:spring-data-commons")
    compileOnly("org.springframework.data:spring-data-jpa")
    compileOnly("org.springframework.modulith:spring-modulith-core")
    compileOnly("org.hibernate.orm:hibernate-core")
    compileOnly("jakarta.persistence:jakarta.persistence-api")
    compileOnly("jakarta.validation:jakarta.validation-api")
    compileOnly("com.fasterxml.jackson.core:jackson-annotations")
    compileOnly("org.springframework.security:spring-security-core")
    compileOnly("org.springframework.security:spring-security-config")
    compileOnly("org.springframework.security:spring-security-web")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect")

    testFixturesCompileOnly(platform("org.springframework.boot:spring-boot-dependencies:${property("springBootVersion")}"))
    testFixturesCompileOnly("org.springframework:spring-context")
    testFixturesCompileOnly("org.springframework.boot:spring-boot-test")
    testFixturesCompileOnly("org.springframework.boot:spring-boot-webtestclient")

    testImplementation(platform("org.springframework.boot:spring-boot-dependencies:${property("springBootVersion")}"))
    testImplementation(platform("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}"))
    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.boot:spring-boot-webmvc-test")
    testImplementation("org.springframework.security:spring-security-core")
    testImplementation("org.springframework.boot:spring-boot-webtestclient")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.modulith:spring-modulith-starter-core")
    testImplementation("org.springframework.modulith:spring-modulith-core")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testRuntimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
    jvmToolchain(21)
}

java {
    withSourcesJar()
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            suppressPomMetadataWarningsFor("testFixturesApiElements")
            suppressPomMetadataWarningsFor("testFixturesRuntimeElements")
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/liqs02/liqs-kit")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
