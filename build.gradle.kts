@file:Suppress("UnstableApiUsage")

import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel


plugins {
    base
    id("idea")
}

tasks.named<UpdateDaemonJvm>("updateDaemonJvm") {
    languageVersion = JavaLanguageVersion.of(24)
}

// Configure IDEA project settings for the proper language level in the IDE
idea {
    project {
        jdkName = "24"
        languageLevel = IdeaLanguageLevel(24)
        targetVersion = "24"
        targetBytecodeVersion = JavaVersion.VERSION_24
        vcs = "Git"

    }
}
