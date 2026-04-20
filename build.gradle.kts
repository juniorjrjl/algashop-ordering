/*buildscript {
	dependencies {
		classpath("org.flywaydb:flyway-database-postgresql:11.14.1")
		classpath("org.flywaydb:flyway-database-postgresql:11.14.1")
	}//plugin flyway
}*/
plugins {
	idea
	java
	jacoco
	alias(libs.plugins.spring.boot)
	alias(libs.plugins.spring.dependency.management)
	alias(libs.plugins.spring.cloud.contract)
	// alias(libs.plugins.flyway)
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


dependencies {
	implementation(libs.java.uuid.generator)
	implementation(libs.commons.validator)
	implementation(libs.hypersistence.tsid)
	implementation(libs.mapstruct)
	implementation(libs.spring.boot.starter.webmvc)
	implementation(libs.spring.boot.starter.data.jpa)
	implementation(libs.spring.boot.starter.validation)
	implementation(libs.spring.boot.restclient)
	implementation(libs.spring.boot.starter.flyway)
	implementation(libs.flyway.database.postgresql)

	compileOnly(libs.lombok)
	runtimeOnly(libs.postgresql)

	annotationProcessor(libs.hibernate.processor)
	annotationProcessor(libs.lombok)
	annotationProcessor(libs.lombok.mapstruct.binding)
	annotationProcessor(libs.mapstruct.processor)

	testCompileOnly(libs.lombok)
	testAnnotationProcessor(libs.lombok)

	testImplementation(libs.spring.boot.starter.flyway.test)
	testImplementation(libs.spring.cloud.starter.contract.verifier)
	testImplementation(libs.rest.assured)
	testImplementation(libs.rest.assured.spring.mock.mvc)
	testImplementation(libs.datafaker)
	testImplementation(libs.assertj.core)
	testImplementation(libs.spring.boot.starter.data.jpa.test)
	testImplementation(libs.spring.boot.starter.webmvc.test)
	testImplementation(libs.wiremock.spring.boot)

	mockitoAgent(libs.mockito.core) { isTransitive = false }
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

tasks.contractTest {
	useJUnitPlatform()

}
