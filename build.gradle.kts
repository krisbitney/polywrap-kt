plugins {
    kotlin("multiplatform") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    id("com.goncalossilva.resources") version "0.2.5"
    id("org.jlleitschuh.gradle.ktlint") version "11.3.1"
    id("org.jetbrains.dokka") version "1.8.20"
    id("convention.publication")
}

group = "io.github.krisbitney"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    val hostOs = System.getProperty("os.name")
    val arch = System.getProperty("os.arch")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> if (arch == "aarch64") {
            macosArm64("native")
        } else {
            macosX64("native")
        }
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.ensarsarajcic.kotlinx:serialization-msgpack:0.5.5")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("com.squareup.okio:okio:3.3.0") // fs plugin
                implementation("io.ktor:ktor-client-core:2.3.0") // http plugin
            }
        }
        val commonTest by getting {
            resources.srcDirs("src/commonMain/resources")
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
                implementation("com.goncalossilva:resources:0.3.2") // access resources in tests
                implementation("io.ktor:ktor-client-mock:2.3.1") // http plugin test
                implementation("com.ionspin.kotlin:bignum:0.3.8") // client test
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1") // client test
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.github.kawamuray.wasmtime:wasmtime-java:0.14.0")
                implementation("io.ktor:ktor-client-android:2.3.0") // http plugin
                implementation("org.slf4j:slf4j-nop:1.7.36") // suppress SLF4J logger warnings
            }
        }
        val nativeMain by getting {
            dependencies {
                implementation("io.github.krisbitney:wasmtime-kt:1.0.0")
                when {
                    hostOs == "Mac OS X" -> implementation("io.ktor:ktor-client-curl:2.3.0")
                    hostOs == "Linux" -> implementation("io.ktor:ktor-client-curl:2.3.0")
                    isMingwX64 -> implementation("io.ktor:ktor-client-winhttp:2.3.0")
                }
            }
        }
    }
}

// javadoc generation for Maven repository publication
tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

// generate dokka html site and copy it to docs folder
tasks.register<Copy>("copyDokkaHtml") {
    dependsOn(tasks.dokkaHtml)
    val docsDir = "$projectDir/docs"
    doFirst { delete(docsDir) }
    from("$buildDir/dokka/html")
    into(docsDir)
}
// automatically generate docs site when publishing
if (!version.toString().endsWith("-SNAPSHOT")) {
    tasks.publish { dependsOn("copyDokkaHtml") }
}

// print stdout during tests
tasks.withType<Test> {
    this.testLogging {
        this.showStandardStreams = true
    }
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    // this rule is not getting picked up in .editorconfig for some reason
    disabledRules.set(setOf("no-wildcard-imports"))
    filter {
        exclude("**/generated/**")
        exclude("**/commonTest/**")
        exclude("**/jvmTest/**")
        exclude("**/jsTest/**")
        exclude("**/nativeTest/**")
        exclude("**/wrap/**")
        exclude("**/wrapHardCoded/**")
    }
}

// ktlint has a bug where 'exclude' does not work, so this is a workaround
tasks {
    listOf(
        runKtlintCheckOverCommonMainSourceSet,
        runKtlintCheckOverCommonTestSourceSet
    ).forEach {
        it {
            setSource(
                project.sourceSets.map { sourceSet ->
                    sourceSet.allSource.filter { file ->
                        !file.path.contains("/generated/") && !file.path.contains("build.gradle.kts")
                    }
                }
            )
        }
    }
}