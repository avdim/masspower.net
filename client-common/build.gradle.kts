import org.gradle.kotlin.dsl.*

plugins {
//    kotlin("jvm")
    id("kotlin-platform-common")
//    id("kotlin2js")//это просто заглушка чтобы компилировалось вместе с stdlib, по хорошему надо kotlin-platform-commo
}

repositories {
    jcenter()
}

dependencies {
    compile(kotlin("stdlib"))//todo try org.jetbrains.kotlin:kotlin-stdlib-common
    compile(project(":server-common"))
}

//dependencies {
//    compile "org.jetbrains.kotlin:kotlin-stdlib-common:$kotlin_version"
//    testCompile "org.jetbrains.kotlin:kotlin-test-annotations-common:$kotlin_version"
//    testCompile "org.jetbrains.kotlin:kotlin-test-common:$kotlin_version"
//}
