plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.jvm)

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    `maven-publish`
    signing
}

group = "io.github.drewlakee"
version = "1.0.3"

publishing {
    publications {
        create<MavenPublication>("mavenJava") {

            group = "io.github.drewlakee"
            artifactId = "jooas"
            version = "1.0.3"

            from(components["java"])

            pom {
                name = "json-object-openapi-schema"
                description = "A Kotlin library for converting Json-object into OpenAPI Schema"
                url = "https://github.com/drewlakee/json-object-openapi-schema"
                licenses {
                    license {
                        name = "MIT License"
                        url = "http://www.opensource.org/licenses/mit-license.php"
                    }
                }
                developers {
                    developer {
                        id = "drewlakee"
                        name = "Andrew Aleynikov"
                        email = "drew.lake@yandex.com"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/drewlakee/json-object-openapi-schema.git"
                    developerConnection = "scm:git:ssh://github.com/drewlakee/json-object-openapi-schema.git"
                    url = "http://github.com/drewlakee/json-object-openapi-schema"
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = project.property("oss.username") as String
                password = project.property("oss.password") as String
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.1")

    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")

    // Use the JUnit 5 integration.
    testImplementation(libs.junit.jupiter.engine)
    testImplementation("org.mockito:mockito-core:3.+")
    testImplementation("org.mockito:mockito-inline:2.13.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api(libs.commons.math3)

    // This dependency is used internally, and not exposed to consumers on their own compile classpath.
    implementation(libs.guava)
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
