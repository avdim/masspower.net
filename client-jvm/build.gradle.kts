import org.gradle.kotlin.dsl.*

plugins {
  application
//  kotlin("jvm")
  id("kotlin-platform-jvm")
  id("kotlinx-serialization") version "0.4.2" apply true
}

repositories {
  mavenCentral()
  jcenter()
  maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
}

if(false)kotlinProject()

application {
  mainClassName = "com.riseofcat.client.jvm.ClientJvmMainJavaAlias"
}

dependencies {
  compile(project(":lib-jvm"))
//  expectedBy(project(":server-common"))
  compile(kotlin("stdlib"))
}
