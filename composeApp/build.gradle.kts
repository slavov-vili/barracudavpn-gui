import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm()

    sourceSets {
        jvmMain.dependencies {
//            implementation("org.jetbrains.compose.runtime:runtime:1.11.0-alpha04")
            implementation("org.jetbrains.compose.foundation:foundation:1.11.0-alpha04")
            implementation("org.jetbrains.compose.material3:material3:1.9.0")
            implementation("org.jetbrains.compose.ui:ui:1.11.0-alpha04")
            implementation("org.jetbrains.compose.components:components-resources:1.11.0-alpha04")
//            implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.11.0-alpha04")
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "de.cas.barracudavpn_gui.MainKt"

        nativeDistributions {
            packageName = "de.cas.barracudavpn_gui"
            packageVersion = "1.0.0"

            modules("java.desktop")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            includeAllModules = false

            linux {
                shortcut = true
                appCategory = "Network"
                menuGroup = "Network"
//                iconFile.set(project.file("src/jvmMain/resources/icon.png"))
            }
        }

        jvmArgs += listOf(
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-opens=java.desktop/sun.awt=ALL-UNNAMED",
            "--add-opens=java.desktop/sun.java2d=ALL-UNNAMED",
            "--enable-native-access=ALL-UNNAMED"
        )
    }
}
