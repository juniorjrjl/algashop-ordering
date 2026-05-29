/*buildscript {
	dependencies {
		classpath("org.flywaydb:flyway-database-postgresql:11.14.1")
		classpath("org.flywaydb:flyway-database-postgresql:11.14.1")
	}//plugin flyway
}*/
plugins {
	// alias(libs.plugins.flyway)
	alias(libs.plugins.spring.boot)
	alias(libs.plugins.spring.cloud.contract)
	alias(libs.plugins.spring.dependency.management)
	idea
	jacoco
	java
}

group = "com.algaworks.algashop"
version = "0.0.1-SNAPSHOT"
description = "Ordering microservice"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

val mockitoAgent = configurations.create("mockitoAgent")
configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}


dependencies {

	annotationProcessor(libs.hibernate.processor)
	annotationProcessor(libs.lombok)
	annotationProcessor(libs.lombok.mapstruct.binding)
	annotationProcessor(libs.mapstruct.processor)

	compileOnly(libs.lombok)

	implementation(libs.commons.validator)
	implementation(libs.flyway.database.postgresql)
	implementation(libs.java.uuid.generator)
	implementation(libs.hypersistence.tsid)
	implementation(libs.mapstruct)
	implementation(libs.spring.boot.restclient)
	implementation(libs.spring.boot.starter.data.jpa)
	implementation(libs.spring.boot.starter.flyway)
	implementation(libs.spring.boot.starter.validation)
	implementation(libs.spring.boot.starter.webmvc)

	mockitoAgent(libs.mockito.core) { isTransitive = false }

	runtimeOnly(libs.postgresql)

	testAnnotationProcessor(libs.lombok)

	testCompileOnly(libs.lombok)

	testImplementation(libs.assertj.core)
	testImplementation(libs.datafaker)
	testImplementation(libs.rest.assured)
	testImplementation(libs.rest.assured.spring.mock.mvc)
	testImplementation(libs.spring.boot.starter.data.jpa.test)
	testImplementation(libs.spring.boot.starter.flyway.test)
	testImplementation(libs.spring.boot.starter.webmvc.test)
	testImplementation(libs.spring.boot.testcontainers)
	testImplementation(libs.spring.cloud.starter.contract.verifier)
	testImplementation(libs.testcontainers.junit.jupiter)
	testImplementation(libs.testcontainers.junit.postgresql)
	testImplementation(libs.wiremock.spring.boot)

	testRuntimeOnly(libs.junit.platform.launcher)

}

dependencyManagement {
	imports {
		mavenBom(libs.spring.cloud.dependencies.get().toString())
	}
}

contracts {
	packageWithBaseClasses = "com.algaworks.algashop.ordering.contract.base"
}

tasks.withType<Test>().configureEach {
	jvmArgs(
		"-javaagent:${configurations.getByName("mockitoAgent").asPath}"
	)
}

tasks.withType<Test> {
	useJUnitPlatform()
	jvmArgs("-javaagent:${mockitoAgent.asPath}")
	finalizedBy(tasks.jacocoTestReport)
	systemProperty("test.seed", System.getProperty("test.seed") ?: "")
}

tasks.register<Test>("integrationTest"){
	description = "Run unit tests."
	group = "verification"

	jvmArgs("-javaagent:${mockitoAgent.asPath}")

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

	jvmArgs("-javaagent:${mockitoAgent.asPath}")

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

tasks.contractTest {
	useJUnitPlatform()

}

tasks.bootJar {
	archiveFileName.set("ordering.jar")
}

tasks.register<Exec>("dockerbuild"){
	description = "Builds a multi-platform Docker image using Buildx"
	group = "build"

	dependsOn("bootJar")

	workingDir = project.rootDir

	commandLine(
		"docker",
		"buildx",
		"build",
		"--platform",
		"linux/arm64/v8,linux/amd64",
		"--tag",
		"algashop/ordering:dev",
		"."
	)
}
