import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.2"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
}

group = "net.nhsd.fhir"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

val springVersion = "2.6.1"
val hapiVersion = "5.6.1"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web:${springVersion}")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("ca.uhn.hapi.fhir:hapi-fhir-converter:${hapiVersion}")
	implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-dstu3:${hapiVersion}")
	implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-r4:${hapiVersion}")
	testImplementation("org.springframework.boot:spring-boot-starter-test:${springVersion}")
	testImplementation("org.assertj:assertj-core:3.21.0")
	testImplementation("com.ninja-squad:springmockk:3.1.0")

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
