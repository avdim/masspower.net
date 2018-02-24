import java.io.*

include("cli")
include("jvm-lib")
project(":jvm-lib").projectDir = File("submodule-server/jvm-lib")
include("client-common")
include("submodule-html")

if(false) {
  includeBuild("submodule-server") {
    dependencySubstitution {
      substitute(module("com.n8cats:core:1.0")).with(project(":"))
    }
  }
}