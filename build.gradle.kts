plugins {
    java
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.sph.hyu.batch"
version = "0.0.1-SNAPSHOT"
description = "hyu-batch - 건설현장 IoT 데이터 배치 (생산성 전송 / 감리 리포트)"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

val querydslVersion = "5.1.0"

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // DB - PostgreSQL + PostGIS (Hibernate 6 spatial 내장, spatial 타입 지원용)
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.hibernate.orm:hibernate-spatial")

    // Util
    implementation("org.apache.commons:commons-lang3")
    implementation("org.modelmapper:modelmapper:3.2.0")

    // QueryDSL (jakarta)
    implementation("com.querydsl:querydsl-jpa:$querydslVersion:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:$querydslVersion:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // DevTools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.batch:spring-batch-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// QueryDSL Q-class 생성 위치
val generatedDir = layout.buildDirectory.dir("generated/sources/annotationProcessor/java/main")
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.generatedSourceOutputDirectory.set(generatedDir.get().asFile)
}

tasks.named<Delete>("clean") {
    delete(generatedDir)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// bootJar 만 산출 (plain jar 비활성화 → Docker COPY 단순화)
tasks.named<Jar>("jar") {
    enabled = false
}
