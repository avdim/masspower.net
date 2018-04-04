import org.gradle.kotlin.dsl.*

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
  mainClassName = "com.riseofcat.service.jvm.ServiceJvmMainJavaAlias"
}

dependencies {
  compile(project(":lib-jvm"))
  compile(project(":heroku-jvm"))
  compile(project(":server-common"))
  compile(project(":desktop"))
  compile(kotlin("stdlib"))
}
