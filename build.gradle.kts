import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    // ------------------------------------------------------------------
    // 网页产物只有一个：Kotlin/Wasm（不配置 js 目标）
    // ------------------------------------------------------------------
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "lingo.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    port = 8080
                    open = true
                }
            }
        }
        binaries.executable()
    }

    // ------------------------------------------------------------------
    // 这个目标只为了能在本机 JVM 上跑单元测试（真实网络连通性测试）。
    // 它不参与网页构建：wasmJsBrowserDistribution 的产物里只有 wasm。
    // ------------------------------------------------------------------
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.compose.resources)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
        }
        jvmTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
        }
    }
}

// 生成的资源访问类是唯一且固定的包名，避免不同情况下包名漂移
compose.resources {
    packageOfResClass = "lingo.generated.resources"
}
