import org.gradle.kotlin.dsl.*

plugins {
  application
  kotlin("jvm")
}

repositories {
  jcenter()
}

if(false)kotlinProject()

application {
  mainClassName = "cli.Main"
}

dependencies {
  compile(project(":core"))
  compile(project(":client-common"))
  //in groovy expectedBy project(":client-common")
  compile(kotlin("stdlib"))
}
