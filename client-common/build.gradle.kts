import org.gradle.kotlin.dsl.*

plugins {
    kotlin("jvm")
//    id("kotlin-platform-common")//todo
}

repositories {
    jcenter()
}

dependencies {
    compile(kotlin("stdlib"))
}

//dependencies {
//    compile "org.jetbrains.kotlin:kotlin-stdlib-common:$kotlin_version"
//    testCompile "org.jetbrains.kotlin:kotlin-test-annotations-common:$kotlin_version"
//    testCompile "org.jetbrains.kotlin:kotlin-test-common:$kotlin_version"
//}
