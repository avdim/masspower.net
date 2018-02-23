import org.gradle.kotlin.dsl.*

plugins {
//  application
  kotlin("jvm")
}

//apply {
//  plugin("kotlin")
//  plugin("kotlin2js")
//}


repositories {
  jcenter()
  mavenCentral()
}

//group = "com.n8cats"
//version = "1.0-SNAPSHOT"
//val kotlin_version:String by extra

dependencies {
  compile(kotlin("stdlib-js"/*,kotlin_version*/))
}
