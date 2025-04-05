package com.github.qczone.switch2trae.actions

import com.github.qczone.switch2trae.settings.AppSettingsState
import com.github.qczone.switch2trae.utils.WindowUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.actionSystem.ActionUpdateThread

class OpenFileInTraeAction : AnAction() {
    private val logger = Logger.getInstance(OpenFileInTraeAction::class.java)

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return
        val virtualFile: VirtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        
        val editor: Editor? = e.getData(CommonDataKeys.EDITOR)
        
        val line = editor?.caretModel?.logicalPosition?.line?.plus(1) ?: 1
        val column = editor?.caretModel?.logicalPosition?.column?.plus(1) ?: 1
        
        val filePath = virtualFile.path
        val settings = AppSettingsState.getInstance()
        val traePath = settings.traePath
        
        val command = when {
            System.getProperty("os.name").lowercase().contains("mac") -> {
                arrayOf("open", "-a", "$traePath", "--args", "--goto", "$filePath:$line:$column")
            }
            System.getProperty("os.name").lowercase().contains("windows") -> {
                arrayOf("cmd", "/c", "$traePath", "--goto", "$filePath:$line:$column")
            }
            else -> {
                arrayOf(traePath, "--goto", "$filePath:$line:$column")
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
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        
        e.presentation.isEnabledAndVisible = project != null && 
                                           virtualFile != null && 
                                           !virtualFile.isDirectory
    }
} 