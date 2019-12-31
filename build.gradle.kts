import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        jcenter()
        fabric()
        gradle()
    }
    dependencies {
        classpath(Dep.fabricPlugin)
        classpath(Dep.gradlePlugin)
        classpath(Dep.kotlinPlugin)
        classpath(Dep.googleServicesPlugin)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }

    // https://github.com/noties/Markwon/issues/148
    configurations.all {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
