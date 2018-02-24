import org.gradle.kotlin.dsl.*

plugins {
  application
//  kotlin("jvm")
  id("kotlin-platform-jvm")
}

repositories {
  jcenter()
}

if(false)kotlinProject()

application {
  mainClassName = "masspower.client.Main"
}

dependencies {
  compile(project(":lib-jvm"))
  expectedBy(project(":server-common"))
  compile(kotlin("stdlib"))
}
