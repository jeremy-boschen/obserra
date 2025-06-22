@file:Suppress("UnstableApiUsage")

plugins {
    base
    id("idea")
}

tasks.named<UpdateDaemonJvm>("updateDaemonJvm") {
    languageVersion = JavaLanguageVersion.of(24)
}
