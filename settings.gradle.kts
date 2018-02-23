import java.io.*

include("cli")
include("core")
project(":core").projectDir = File("submodule-server/core")
include("kotlin-js")

if(false) {
  includeBuild("submodule-server") {
    dependencySubstitution {
      substitute(module("com.n8cats:core:1.0")).with(project(":"))
    }
  }
}