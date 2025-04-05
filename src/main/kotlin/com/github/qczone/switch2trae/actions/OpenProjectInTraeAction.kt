package com.github.qczone.switch2trae.actions

import com.github.qczone.switch2trae.settings.AppSettingsState
import com.github.qczone.switch2trae.utils.WindowUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.actionSystem.ActionUpdateThread

class OpenProjectInTraeAction : AnAction() {
    private val logger = Logger.getInstance(OpenProjectInTraeAction::class.java)

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return
        val projectPath = project.basePath ?: return

        val settings = AppSettingsState.getInstance()
        val traePath = settings.traePath

        val command = when {
            System.getProperty("os.name").lowercase().contains("mac") -> {
                arrayOf("open", "-a", "$traePath", "--args", projectPath)
            }

            System.getProperty("os.name").lowercase().contains("windows") -> {
                arrayOf("cmd", "/c", "$traePath", projectPath)
            }

            else -> {
                arrayOf(traePath, projectPath)
            }
        }
        try {
            logger.info("Executing command: ${command.joinToString(" ")}")
            ProcessBuilder(*command).start()
        } catch (ex: Exception) {
            logger.error("Failed to execute Trae command: ${ex.message}", ex)
            com.intellij.openapi.ui.Messages.showErrorDialog(
                project,
                """
                ${ex.message}
                
                Please check:
                1. Trae path is correctly configured in Settings > Tools > switch2trae
                2. Trae is properly installed on your system
                3. The configured path points to a valid Trae executable
                """.trimIndent(),
                "Error"
            )
        }

        WindowUtils.activeWindow()
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
    }
} 