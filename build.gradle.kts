plugins {
	java
	id("org.springframework.boot") version "3.0.2"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation ("org.springframework.boot:spring-boot-starter-data-jpa")
	// implementation ("org.springframework.security:spring-security-core") // TODO: Use the security library to handle authentication and authorization
	// implementation ("org.springframework.security:spring-security-config") // TODO: Use the security library to handle authentication and authorization
	// implementation ("org.springframework.security:spring-security-web") // TODO: Use the security library to handle authentication and authorization

	implementation ("org.springframework.data:spring-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.postgresql:postgresql")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
