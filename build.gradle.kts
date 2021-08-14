import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  application
  id("com.github.johnrengelman.shadow") version "7.0.0"
  kotlin("jvm") version "1.5.21"
  kotlin("plugin.serialization") version "1.5.21"
}

group = "com.painkillergis"
version = ProcessBuilder("sh", "-c", "git rev-list --count HEAD")
  .start()
  .apply { waitFor() }
  .inputStream.bufferedReader().readText().trim()

application {
  mainClass.set("com.painkillergis.fall_color_history.ApplicationKt")
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("ch.qos.logback:logback-classic:+")
  implementation("io.ktor:ktor-serialization:+")
  implementation("io.ktor:ktor-server-core:+")
  implementation("io.ktor:ktor-server-netty:+")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:+")
  runtimeOnly("org.xerial:sqlite-jdbc:+")
  testImplementation("io.kotest:kotest-assertions-core:+")
  testImplementation("io.kotest:kotest-runner-junit5:+")
  testImplementation("io.ktor:ktor-client-cio:+")
  testImplementation("io.ktor:ktor-client-core:+")
  testImplementation("io.ktor:ktor-client-serialization:+")
  testImplementation("io.ktor:ktor-server-tests:+")
  testImplementation("io.mockk:mockk:+")
  testImplementation("org.jetbrains.kotlin:kotlin-test:+")
}

configurations.all {
  resolutionStrategy {
    activateDependencyLocking()
    componentSelection
      .all(object : Action<ComponentSelection> {
        @Mutate
        override fun execute(selection: ComponentSelection) {
          val version = selection.candidate.version
          when {
            version.matches(
              Regex(
                ".*[-.]rc\\d*$",
                RegexOption.IGNORE_CASE
              )
            ) -> selection.reject("Release candidates are excluded")
            version.matches(Regex(".*-M\\d+$")) -> selection.reject("Milestones are excluded")
            version.matches(Regex(".*-alpha\\d+$")) -> selection.reject("Alphas are excluded")
            version.matches(Regex(".*-beta\\d+$")) -> selection.reject("Betas are excluded")
          }
        }
      })
  }
}

tasks.jar {
  enabled = false
}

tasks.withType<ShadowJar> {
  archiveBaseName.set("fall-color-history")
  archiveClassifier.set("")
  archiveVersion.set("")
}

tasks.processResources {
  doLast {
    File("$buildDir/resources/main/version").writeText(version.toString())
  }
}

tasks.test {
  useJUnitPlatform()
}