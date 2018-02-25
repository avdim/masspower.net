import java.io.*

include("client-jvm")
include("lib-jvm")
project(":lib-jvm").projectDir = File("submodule-server/lib-jvm")
include("server-common")
project(":server-common").projectDir = File("submodule-server/server-common")
include("client-common")
include("submodule-html")
include("desktop")
include("core")

if(false) {
  includeBuild("submodule-server") {
    dependencySubstitution {
      substitute(module("com.n8cats:core:1.0")).with(project(":"))
    }
  }
}