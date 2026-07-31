plugins {
	java
	id("org.springframework.boot") version "4.1.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.google.protobuf") version "0.9.4"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-kafka")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:3.0.3")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-kafka-test")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")

	implementation("io.grpc:grpc-netty-shaded")
	implementation("io.grpc:grpc-protobuf")
	implementation("io.grpc:grpc-stub")
	implementation("org.apache.tomcat:annotations-api:6.0.53")
	implementation("com.google.protobuf:protobuf-java:4.36.0-RC1")
}

dependencyManagement {
	imports {
		// Alinha TODOS os módulos io.grpc:* na mesma versão
		// (declarados sem versão nas dependencies acima).
		mavenBom("io.grpc:grpc-bom:1.82.2")
	}
}

protobuf {
	protoc {
		// Deve corresponder à versão de protobuf-java
		artifact = "com.google.protobuf:protoc:4.36.0-RC1"
	}
	// O plugin já regista e aplica um locator "grpc"
	// (artifact io.grpc:protoc-gen-grpc-java, sem versão);
	// aqui só fixamos a versão para corresponder ao grpc.
	plugins {
		getByName("grpc").artifact = "io.grpc:protoc-gen-grpc-java:1.82.2"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
