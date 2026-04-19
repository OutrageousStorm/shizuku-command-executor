# ⚡ Shizuku Command Executor

Kotlin Android library for executing shell commands via Shizuku without root.

## Setup

```gradle
dependencies {
    implementation 'moe.shizuku.privilege:api:13.1.0'
    implementation 'com.github.OutrageousStorm:shizuku-command-executor:1.0.0'
}
```

## Usage

```kotlin
import com.outrageousstorm.shizuku.ShizukuExecutor

// Grant Shizuku permission in app first
val executor = ShizukuExecutor()

// Simple command
val result = executor.exec("pm list packages")
result.lines().forEach { println(it) }

// With error handling
try {
    val output = executor.exec("settings get system screen_brightness")
    println("Current brightness: $output")
} catch (e: ShizukuNotAvailableException) {
    println("Shizuku not running")
}

// Revoke permissions
executor.exec("pm revoke com.facebook.katana android.permission.LOCATION")

// Set settings
executor.exec("settings put system screen_brightness 200")
```
