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
  mainClassName = "cli.Main"
}

dependencies {
  compile(project(":jvm-lib"))
  expectedBy(project(":server-common"))
  compile(kotlin("stdlib"))
}
