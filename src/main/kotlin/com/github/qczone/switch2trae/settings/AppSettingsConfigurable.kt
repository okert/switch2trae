package com.github.qczone.switch2trae.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder

class AppSettingsConfigurable : Configurable {
    private var mySettingsComponent: AppSettingsComponent? = null

    override fun getDisplayName(): String = "Open In Trae"

    override fun createComponent(): JComponent {
        mySettingsComponent = AppSettingsComponent()
        return mySettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings = AppSettingsState.getInstance()
        return mySettingsComponent!!.traePath != settings.traePath
    }

    override fun apply() {
        val settings = AppSettingsState.getInstance()
        settings.traePath = mySettingsComponent!!.traePath
    }

    override fun reset() {
        val settings = AppSettingsState.getInstance()
        mySettingsComponent!!.traePath = settings.traePath
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}

class AppSettingsComponent {
    val panel: JPanel
    private val traePathText = JTextField()

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Trae Path: "), traePathText, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    var traePath: String
        get() = traePathText.text
        set(value) {
            traePathText.text = value
        }
} 