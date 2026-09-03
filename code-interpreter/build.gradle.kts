plugins {
    id("rikkahub.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "me.rerere.rikkahub.codeinterpreter"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
