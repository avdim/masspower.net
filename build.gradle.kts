val kotlinVersion by project
println("kotlinVersion: $kotlinVersion")

plugins {
  base
  kotlin("jvm") version "1.2.30" apply false
}
allprojects {
  group = "io.mass-power"
  version = "1.1"//todo нужна ли версия всем проектам? Наверное только клиентам.
  if(false) {
    repositories {
      jcenter()
    }
  }
}
dependencies {
  // Make the root project archives configuration depend on every sub-project
  subprojects.forEach {
    archives(it)
  }
}
