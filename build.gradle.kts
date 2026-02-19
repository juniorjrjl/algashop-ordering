plugins {
	id("idea")
	java
	id("org.springframework.boot") version "4.0.1"
	id("io.spring.dependency-management") version "1.1.7"
	jacoco
}

group = "com.algaworks.algashop"
version = "0.0.1-SNAPSHOT"
description = "Ordering microservice"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

val mockitoAgent: Configuration = configurations.create("mockitoAgent")
configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

val mapstructVersion = "1.6.3"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-restclient")
	implementation("com.fasterxml.uuid:java-uuid-generator:5.2.0")
	implementation("commons-validator:commons-validator:1.10.1")
	implementation("io.hypersistence:hypersistence-tsid:2.1.4")
	implementation("org.mapstruct:mapstruct:$mapstructVersion")

	compileOnly("org.projectlombok:lombok")

	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
	annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
	annotationProcessor("org.hibernate.orm:hibernate-processor")

	implementation("org.springframework.boot:spring-boot-h2console:4.0.1")
	runtimeOnly("com.h2database:h2")

	testCompileOnly("org.projectlombok:lombok")

	testAnnotationProcessor("org.projectlombok:lombok")

	testImplementation("org.wiremock.integrations:wiremock-spring-boot:4.0.9")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("net.datafaker:datafaker:2.5.2")
	testImplementation("org.assertj:assertj-core:3.27.7")
	mockitoAgent("org.mockito:mockito-core"){
		isTransitive = false
	}

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test>().configureEach {
	jvmArgs(
		"-javaagent:${configurations.getByName("mockitoAgent").asPath}"
	)
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
	systemProperty("test.seed", System.getProperty("test.seed") ?: "")
}

tasks.register<Test>("integrationTest"){
	description = "Run unit tests."
	group = "verification"

	testClassesDirs = tasks.test.get().testClassesDirs
	classpath = tasks.test.get().classpath

	useJUnitPlatform{
		includeTags("IntegrationTest")
	}
	systemProperty("test.seed", System.getProperty("test.seed") ?: "")
}

tasks.register<Test>("unitTest"){
	description = "Run unit tests."
	group = "verification"

	testClassesDirs = tasks.test.get().testClassesDirs
	classpath = tasks.test.get().classpath

	useJUnitPlatform{
		includeTags("UnitTest")
	}
	systemProperty("test.seed", System.getProperty("test.seed") ?: "")
}

tasks.jacocoTestReport {
	reports {
		xml.required = false
		csv.required = false
		html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
	}
}
