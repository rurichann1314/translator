pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        // 国内镜像，优先命中
        maven("https://maven.aliyun.com/repository/gradle-plugin")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://maven.aliyun.com/repository/public")
    }
}

rootProject.name = "lingo"
