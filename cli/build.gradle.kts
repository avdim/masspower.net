plugins {
  application
  kotlin("jvm")
}

if(false)kotlinProject()

application {
  mainClassName = "cli.Main"
}

dependencies {
  compile(project(":core"))
  compile(kotlin("stdlib"))
}
