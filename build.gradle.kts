import org.gradle.api.tasks.Exec

plugins {
    application
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "vcs"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
}

application {
    mainClass.set("org.vcs.solarproxytester.Main")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

tasks.shadowJar {
    archiveFileName.set("${project.name}-${project.version}.jar")
}

tasks.register("prepareBuildLibs") {
    doFirst {
        val libsDir = file("build/libs")
        if (libsDir.exists()) {
            libsDir.deleteRecursively()
        }
        libsDir.mkdirs()
    }
}

tasks.named("distZip") {
    dependsOn(tasks.named("shadowJar"))
}

tasks.named("distTar") {
    dependsOn(tasks.named("shadowJar"))
}

tasks.named("startScripts") {
    dependsOn(tasks.named("shadowJar"))
}

tasks.named("startShadowScripts") {
    dependsOn(tasks.named("jar"))
}

tasks.shadowJar {
    dependsOn("prepareBuildLibs")
    archiveFileName.set("${project.name}-${project.version}.jar")
}

tasks.register<Exec>("buildDockerImage") {
    group = "docker"
    description = "Builds the Docker image and saves it to a file."

    dependsOn("shadowJar")

    val version = project.version.toString()

    commandLine("docker", "build", "-t", "ghcr.io/sindram/solar-proxy-tester:$version", ".")

    doLast {
        exec {
            commandLine("docker", "save", "-o", "build/libs/solarproxytester_image_${version}.tar", "ghcr.io/sindram/solar-proxy-tester:$version")
        }
    }
}

tasks.register<Exec>("pushDockerImage") {
    group = "docker"
    description = "Tags and pushes the Docker image to GitHub Container Registry."

    dependsOn("buildDockerImage")
    dependsOn("dockerLogin")

    val githubUsername = System.getenv("GITHUB_USERNAME") ?: error("GITHUB_USERNAME is not set")
    val repositoryName = "solar-proxy-tester"
    val version = project.version.toString()

    commandLine("docker", "push", "ghcr.io/$githubUsername/$repositoryName:$version")
}

tasks.register<Exec>("dockerLogin") {
    group = "docker"
    description = "Logs in to GitHub Container Registry."

    val githubToken = System.getenv("GITHUB_TOKEN") ?: error("GITHUB_TOKEN is not set")
    val githubUsername = System.getenv("GITHUB_USERNAME") ?: error("GITHUB_USERNAME is not set")

    commandLine("docker", "login", "ghcr.io", "-u", githubUsername, "--password-stdin")
    standardInput = githubToken.byteInputStream()

}


