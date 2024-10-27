import io.gitlab.arturbosch.detekt.Detekt
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
  alias(libs.plugins.detekt)
  alias(libs.plugins.ktlint)
}

android {
  namespace = "com.github.crisacm.xmlpaging3"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.github.crisacm.xmlpaging3"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_17.toString()
  }
  buildFeatures {
    viewBinding = true
  }
}

// Config detekt
detekt {
  baseline = file("$rootDir/config/detekt/detekt-baseline.xml")
}

// Create custom detekt task
tasks.register("detektProject", Detekt::class) {
  val autoFix = project.hasProperty("detektAutoFix")
  autoCorrect = autoFix

  description = "Overrides current detekt task to execute same config in all modules."
  buildUponDefaultConfig = true
  ignoreFailures = false
  parallel = true
  setSource(file(projectDir))
  baseline.set(file("$rootDir/config/detekt/detekt-baseline.xml"))
  config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
  include("**/*.kt", "**/*.kts")
  exclude("**/resources/**", "**/build/**")
  reports {
    // Enable/Disable HTML report (default: true)
    md.required.set(false)
    txt.required.set(false)
    sarif.required.set(false)
    xml.required.set(true)
    html.required.set(true)
  }
}

ktlint {
  android.set(true)
  debug.set(true)
  outputToConsole.set(false)
  ignoreFailures.set(false)
  enableExperimentalRules.set(true)
  baseline.set(file("$rootDir/config/ktlint/baseline.xml"))
  disabledRules.addAll("final-newline", "no-wildcard-imports")
  reporters {
    reporter(ReporterType.HTML)
    reporter(ReporterType.JSON)
  }
  filter {
    exclude("**/generated/**")
    include("**/kotlin/**")
  }
}

// Analise detekt and ktlint before build
tasks
  .getByPath("preBuild")
  .dependsOn("detektProject")
  .dependsOn("ktlintCheck")

dependencies {

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.firebase.database.ktx)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)

  // Retrofit, Moshi, Okhtpp
  implementation("com.squareup.retrofit2:retrofit:2.11.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
  implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
  implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

  // Paging
  implementation("androidx.paging:paging-runtime-ktx:3.3.2")

  // Hilt
  implementation("com.google.dagger:hilt-android:2.51.1")
  ksp("com.google.dagger:hilt-android-compiler:2.51")

  // Room
  implementation("androidx.room:room-runtime:2.6.1")
  implementation("androidx.room:room-ktx:2.6.1")
  ksp("androidx.room:room-compiler:2.6.1")
  implementation("androidx.room:room-paging:2.6.1")
}
