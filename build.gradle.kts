// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  id("com.android.application") version libs.versions.pluginGradle apply false
  id("org.jetbrains.kotlin.android") version libs.versions.kotlin apply false
  id("org.jetbrains.kotlin.plugin.compose") version libs.versions.kotlin apply false
  id("com.google.dagger.hilt.android") version libs.versions.hilt apply false
  id("com.google.devtools.ksp") version libs.versions.pluginKsp apply false
  id("androidx.room") version libs.versions.room apply false
}
