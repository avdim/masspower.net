import java.io.*

include("client-jvm")
include("jvm-lib")
project(":jvm-lib").projectDir = File("submodule-server/jvm-lib")
include("server-common")
project(":server-common").projectDir = File("submodule-server/server-common")
include("client-common")
include("submodule-html")

if(false) {
  includeBuild("submodule-server") {
    dependencySubstitution {
      substitute(module("com.n8cats:core:1.0")).with(project(":"))
    }
  }
}