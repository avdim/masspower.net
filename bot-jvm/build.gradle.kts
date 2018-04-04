import org.gradle.kotlin.dsl.*

val serialization_version = "0.4.2" //todo delete ?
val gdxVersion by project //todo delete

plugins {
  application
  id("kotlin-platform-jvm")
  id("kotlinx-serialization") version "0.4.2" apply true
}

repositories {
  mavenCentral()
  jcenter()
  maven { url = uri("http://dl.bintray.com/kotlin/ktor") }
  maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
}

if(false)kotlinProject()

application {
  mainClassName = "com.riseofcat.bot.BotJvmMainJavaAlias"
}

dependencies {
  compile(project(":core"))//todo delete
  compile(project(":lib-jvm"))
  compile(project(":server-common"))
  compile(kotlin("stdlib"))

  compile("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")//todo delete
  compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serialization_version")
}
